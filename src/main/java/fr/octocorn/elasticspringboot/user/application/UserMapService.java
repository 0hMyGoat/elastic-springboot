package fr.octocorn.elasticspringboot.user.application;

import co.elastic.clients.elasticsearch._types.aggregations.GeoCentroidAggregate;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import fr.octocorn.elasticspringboot.user.application.view.MapPointView;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserDocument;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserMapQueryBuilder;
import co.elastic.clients.elasticsearch._types.aggregations.GeoHashGridBucket;
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

    private static final int ZOOM_INDIVIDUAL_THRESHOLD = 14;

    private final ElasticsearchOperations elasticsearchOperations;
    private final UserMapQueryBuilder queryBuilder;

    // ── Points carte ─────────────────────────────────────────────────────────

    /**
     * Retourne les points à afficher selon le niveau de zoom.
     * En dessous du seuil : clusters geohash. Au-dessus : individus.
     *
     * @param zoomLevel niveau de zoom frontend (ex : 5–18)
     * @param jobId     filtre optionnel par métier
     * @param sectorId  filtre optionnel par secteur
     * @param minLat    borne sud (requis en mode individus)
     * @param maxLat    borne nord (requis en mode individus)
     * @param minLon    borne ouest (requis en mode individus)
     * @param maxLon    borne est (requis en mode individus)
     */
    public List<MapPointView> getClusters(int zoomLevel, UUID jobId, UUID sectorId,
                                          Double minLat, Double maxLat,
                                          Double minLon, Double maxLon) {
        if (zoomLevel >= ZOOM_INDIVIDUAL_THRESHOLD) {
            return getIndividuals(jobId, sectorId, minLat, maxLat, minLon, maxLon);
        }
        return aggregerParGeohash(toGeohashPrecision(zoomLevel), jobId, sectorId);
    }

    // ── Détail d'un cluster ───────────────────────────────────────────────────

    /**
     * Retourne les utilisateurs appartenant à une cellule geohash.
     *
     * @param geohashCell cellule geohash (ex : "u09t")
     * @param jobId       filtre optionnel par métier
     * @param sectorId    filtre optionnel par secteur
     */
    public List<UserSearchResult> getClusterUsers(String geohashCell, UUID jobId, UUID sectorId) {
        NativeQuery query = queryBuilder.buildGeohashCellQuery(geohashCell, jobId, sectorId);
        return elasticsearchOperations.search(query, UserDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .map(this::toUserSearchResult)
                .toList();
    }

    // ── Implémentations privées ───────────────────────────────────────────────

    private List<MapPointView> aggregerParGeohash(int precision, UUID jobId, UUID sectorId) {
        NativeQuery query = queryBuilder.buildGeohashGridQuery(precision, jobId, sectorId);
        SearchHits<UserDocument> hits = elasticsearchOperations.search(query, UserDocument.class);

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();
        if (aggregations == null) return List.of();

        var byGeohash = aggregations.aggregationsAsMap().get("by_geohash");
        if (byGeohash == null) return List.of();

        List<GeoHashGridBucket> buckets = byGeohash.aggregation().getAggregate()
                .geohashGrid().buckets().array();

        return buckets.stream()
                .map(this::toMapPoint)
                .filter(Objects::nonNull)
                .toList();
    }

    private MapPointView toMapPoint(GeoHashGridBucket bucket) {
        GeoCentroidAggregate centroid = bucket.aggregations().get("centroid").geoCentroid();
        if (centroid.location() == null) return null;
        if (!centroid.location().isLatlon()) return null;

        double lat = centroid.location().latlon().lat();
        double lon = centroid.location().latlon().lon();
        return new MapPointView(bucket.key(), bucket.docCount(), lat, lon, null);
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

    /**
     * Mappe un niveau de zoom frontend vers une précision geohash (1–12).
     * Plus le zoom est élevé, plus la précision est fine.
     */
    private int toGeohashPrecision(int zoomLevel) {
        if (zoomLevel <= 3)  return 2;
        if (zoomLevel <= 6)  return 3;
        if (zoomLevel <= 9)  return 4;
        if (zoomLevel <= 11) return 5;
        if (zoomLevel <= 13) return 6;
        return 7;
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
