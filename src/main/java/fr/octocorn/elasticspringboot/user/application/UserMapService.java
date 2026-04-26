package fr.octocorn.elasticspringboot.user.application;

import co.elastic.clients.elasticsearch._types.aggregations.GeoCentroidAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import fr.octocorn.elasticspringboot.user.application.view.MapPointView;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserDocument;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserMapQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMapService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final UserMapQueryBuilder queryBuilder;

    // ── Points carte (précision variable) ───────────────────────────────────

    /**
     * Retourne les points à afficher selon le niveau de précision demandé.
     *
     * @param precision niveau de zoom/précision
     * @param jobId     filtre optionnel par métier
     * @param sectorId  filtre optionnel par secteur
     * @param minLat    borne sud (requis si INDIVIDUAL)
     * @param maxLat    borne nord (requis si INDIVIDUAL)
     * @param minLon    borne ouest (requis si INDIVIDUAL)
     * @param maxLon    borne est (requis si INDIVIDUAL)
     */
    public List<MapPointView> getClusters(MapPrecision precision, UUID jobId, UUID sectorId,
                                          Double minLat, Double maxLat,
                                          Double minLon, Double maxLon) {
        return switch (precision) {
            case CITY -> aggregerPar("city", jobId, sectorId);
            case POSTAL_CODE -> aggregerPar("postalCode", jobId, sectorId);
            case INDIVIDUAL -> getIndividuals(jobId, sectorId, minLat, maxLat, minLon, maxLon);
        };
    }

    // ── Détail d'un cluster (liste des utilisateurs) ─────────────────────────

    /**
     * Retourne les utilisateurs appartenant à un cluster (ville ou code postal).
     *
     * @param precision CITY ou POSTAL_CODE (INDIVIDUAL non applicable)
     * @param value     valeur du cluster (ex : "montréal")
     * @param jobId     filtre optionnel par métier
     * @param sectorId  filtre optionnel par secteur
     */
    public List<UserSearchResult> getClusterUsers(MapPrecision precision, String value,
                                                   UUID jobId, UUID sectorId) {
        String field = precision == MapPrecision.POSTAL_CODE ? "postalCode" : "city";
        NativeQuery query = queryBuilder.buildClusterUsersQuery(field, value, jobId, sectorId);
        return elasticsearchOperations.search(query, UserDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .map(this::toUserSearchResult)
                .toList();
    }

    // ── Implémentations privées ──────────────────────────────────────────────

    private List<MapPointView> aggregerPar(String field, UUID jobId, UUID sectorId) {
        NativeQuery query = queryBuilder.buildAggregationQuery(field, jobId, sectorId);
        SearchHits<UserDocument> hits = elasticsearchOperations.search(query, UserDocument.class);

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();
        if (aggregations == null) return List.of();

        var byField = aggregations.aggregationsAsMap().get("by_" + field);
        if (byField == null) return List.of();

        List<StringTermsBucket> buckets = byField.aggregation().getAggregate()
                .sterms().buckets().array();

        return buckets.stream()
                .map(this::toMapPoint)
                .filter(Objects::nonNull)
                .toList();
    }

    private MapPointView toMapPoint(StringTermsBucket bucket) {
        GeoCentroidAggregate centroid = bucket.aggregations().get("centroid").geoCentroid();
        if (centroid.location() == null) return null;
        if (!centroid.location().isLatlon()) return null;

        double lat = centroid.location().latlon().lat();
        double lon = centroid.location().latlon().lon();
        // bucket.key() retourne FieldValue pour un StringTermsBucket dans co.elastic.clients
        return new MapPointView(bucket.key().stringValue(), bucket.docCount(), lat, lon, null);
    }

    private List<MapPointView> getIndividuals(UUID jobId, UUID sectorId,
                                               Double minLat, Double maxLat,
                                               Double minLon, Double maxLon) {
        NativeQuery query = queryBuilder.buildIndividualQuery(jobId, sectorId, minLat, maxLat, minLon, maxLon);
        return elasticsearchOperations.search(query, UserDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .map(doc -> {
                    if (doc.getLocation() == null) return null;
                    return new MapPointView(
                            doc.getFirstName() + " " + doc.getLastName(),
                            1,
                            doc.getLocation().getLat(),
                            doc.getLocation().getLon(),
                            doc.getId().toString()
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private UserSearchResult toUserSearchResult(UserDocument doc) {
        return new UserSearchResult(
                doc.getId(),
                doc.getFirstName(),
                doc.getLastName(),
                doc.getCity(),
                doc.getPostalCode(),
                doc.getLocation(),
                doc.getJobName(),
                doc.getSectorName(),
                doc.getEmails(),
                doc.getPhones()
        );
    }
}








