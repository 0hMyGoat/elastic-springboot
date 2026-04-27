package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.GeoHashPrecision;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Construit les requêtes Elasticsearch dédiées à la carte géographique.
 * Deux modes : agrégation geohash_grid ou liste individuelle avec bounding box.
 */
@Component
public class UserMapQueryBuilder {

    /**
     * Construit une requête d'agrégation geohash_grid + geo_centroid.
     *
     * @param precision longueur du geohash (1–12)
     * @param jobId     filtre optionnel par métier
     * @param sectorId  filtre optionnel par secteur
     */
    public NativeQuery buildGeohashGridQuery(int precision, UUID jobId, UUID sectorId) {
        BoolQuery.Builder bool = buildBaseFilter(jobId, sectorId);

        return NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withAggregation("by_geohash", Aggregation.of(a -> a
                        .geohashGrid(g -> g
                                .field("location")
                                .precision(GeoHashPrecision.of(p -> p.geohashLength(precision)))
                        )
                        .aggregations("centroid", Aggregation.of(sub -> sub
                                .geoCentroid(gc -> gc.field("location"))
                        ))
                ))
                .withMaxResults(0)
                .build();
    }

    /**
     * Construit une requête retournant les utilisateurs d'une cellule geohash.
     * Decode le geohash en bounding box et utilise un filtre geo_bounding_box.
     *
     * @param geohashCell cellule geohash exacte (ex : "u09t")
     * @param jobId       filtre optionnel par métier
     * @param sectorId    filtre optionnel par secteur
     */
    public NativeQuery buildGeohashCellQuery(String geohashCell, UUID jobId, UUID sectorId) {
        BoolQuery.Builder bool = buildBaseFilter(jobId, sectorId);

        // Décode le geohash en bounding box
        GeohashBounds bounds = decodeGeohashToBounds(geohashCell);

        // Applique un filtre geo_bounding_box
        bool.filter(q -> q.geoBoundingBox(gb -> gb
                .field("location")
                .boundingBox(bb -> bb.tlbr(tlbr -> tlbr
                        .topLeft(tl -> tl.latlon(ll -> ll.lat(bounds.maxLat).lon(bounds.minLon)))
                        .bottomRight(br -> br.latlon(ll -> ll.lat(bounds.minLat).lon(bounds.maxLon)))
                ))
        ));

        return NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withMaxResults(200)
                .build();
    }

    // ── Décodage Geohash ────────────────────────────────────────────────────────

    private record GeohashBounds(double minLat, double maxLat, double minLon, double maxLon) {}

    private GeohashBounds decodeGeohashToBounds(String geohash) {
        double[] latRange = {-85.05112878, 85.05112878};
        double[] lonRange = {-180, 180};
        boolean isLon = true;

        for (char c : geohash.toCharArray()) {
            int idx = BASE32.indexOf(c);
            if (idx == -1) idx = 0;

            for (int i = 4; i >= 0; i--) {
                int bit = (idx >> i) & 1;
                if (isLon) {
                    double mid = (lonRange[0] + lonRange[1]) / 2;
                    if (bit == 1) lonRange[0] = mid;
                    else lonRange[1] = mid;
                } else {
                    double mid = (latRange[0] + latRange[1]) / 2;
                    if (bit == 1) latRange[0] = mid;
                    else latRange[1] = mid;
                }
                isLon = !isLon;
            }
        }

        return new GeohashBounds(latRange[0], latRange[1], lonRange[0], lonRange[1]);
    }

    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    /**
     * Construit une requête retournant les individus dans une bounding box donnée.
     * Limite à 200 résultats pour éviter de surcharger le frontend.
     *
     * @param jobId    filtre optionnel par métier
     * @param sectorId filtre optionnel par secteur
     * @param minLat   borne sud (optionnel)
     * @param maxLat   borne nord (optionnel)
     * @param minLon   borne ouest (optionnel)
     * @param maxLon   borne est (optionnel)
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

    // ── Helpers privés ───────────────────────────────────────────────────────

    private BoolQuery.Builder buildBaseFilter(UUID jobId, UUID sectorId) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
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
