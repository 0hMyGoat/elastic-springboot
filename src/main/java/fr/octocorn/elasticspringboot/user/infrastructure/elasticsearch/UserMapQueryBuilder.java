package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Construit les requêtes Elasticsearch dédiées à la carte géographique.
 * Trois modes : agrégation par ville, par code postal, ou liste individuelle.
 */
@Component
public class UserMapQueryBuilder {

    // ── Agrégation (CITY / POSTAL_CODE) ─────────────────────────────────────

    /**
     * Construit une requête d'agrégation terms + geo_centroid sur le champ donné.
     *
     * @param field    "city" ou "postalCode"
     * @param jobId    filtre optionnel par métier
     * @param sectorId filtre optionnel par secteur
     */
    public NativeQuery buildAggregationQuery(String field, UUID jobId, UUID sectorId) {
        BoolQuery.Builder bool = buildBaseFilter(jobId, sectorId);

        return NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withAggregation("by_" + field, Aggregation.of(a -> a
                        .terms(t -> t.field(field).size(1000))
                        .aggregations("centroid", Aggregation.of(sub -> sub
                                .geoCentroid(gc -> gc.field("location"))
                        ))
                ))
                .withMaxResults(0)
                .build();
    }

    // ── Individus (INDIVIDUAL) ───────────────────────────────────────────────

    /**
     * Construit une requête retournant les individus dans une bounding box donnée.
     * Limite à 200 résultats pour éviter de surcharger le frontend.
     *
     * @param jobId    filtre optionnel par métier
     * @param sectorId filtre optionnel par secteur
     * @param minLat   borne sud de la carte visible (optionnel)
     * @param maxLat   borne nord de la carte visible (optionnel)
     * @param minLon   borne ouest de la carte visible (optionnel)
     * @param maxLon   borne est de la carte visible (optionnel)
     */
    public NativeQuery buildIndividualQuery(UUID jobId, UUID sectorId,
                                            Double minLat, Double maxLat,
                                            Double minLon, Double maxLon) {
        BoolQuery.Builder bool = buildBaseFilter(jobId, sectorId);

        if (minLat != null && maxLat != null && minLon != null && maxLon != null) {
            bool.filter(q -> q.geoBoundingBox(gb -> gb
                    .field("location")
                    .boundingBox(bb -> bb.tlbr(tlbr -> tlbr
                            .topLeft(tl -> tl.latlon(ll -> ll.lat(maxLat).lon(minLon)))
                            .bottomRight(br -> br.latlon(ll -> ll.lat(minLat).lon(maxLon)))
                    ))
            ));
        }

        return NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withMaxResults(200)
                .build();
    }

    // ── Détail cluster ───────────────────────────────────────────────────────

    /**
     * Construit une requête retournant les utilisateurs d'un cluster (ville ou code postal).
     *
     * @param field    "city" ou "postalCode"
     * @param value    valeur exacte du cluster (ex : "montréal")
     * @param jobId    filtre optionnel par métier
     * @param sectorId filtre optionnel par secteur
     */
    public NativeQuery buildClusterUsersQuery(String field, String value, UUID jobId, UUID sectorId) {
        BoolQuery.Builder bool = buildBaseFilter(jobId, sectorId);
        bool.filter(q -> q.term(t -> t.field(field).value(value)));

        return NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withMaxResults(100)
                .build();
    }

    // ── Helpers privés ───────────────────────────────────────────────────────

    private BoolQuery.Builder buildBaseFilter(UUID jobId, UUID sectorId) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
        // Exclure les utilisateurs sans coordonnées géographiques
        bool.filter(q -> q.exists(e -> e.field("location")));
        ajouterFiltreUuid(bool, "jobId", jobId);
        ajouterFiltreUuid(bool, "sectorId", sectorId);
        return bool;
    }

    private void ajouterFiltreUuid(BoolQuery.Builder bool, String field, UUID value) {
        if (value == null) return;
        bool.filter(q -> q.term(t -> t.field(field).value(value.toString())));
    }
}

