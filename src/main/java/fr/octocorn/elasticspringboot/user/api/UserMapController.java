package fr.octocorn.elasticspringboot.user.api;

import fr.octocorn.elasticspringboot.user.application.UserMapService;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import fr.octocorn.elasticspringboot.user.application.view.MapPointView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Users", description = "Carte géographique des utilisateurs")
@RestController
@RequestMapping("/users/map")
@RequiredArgsConstructor
public class UserMapController {

    private final UserMapService userMapService;

    @Operation(
            summary = "Points géographiques pour la carte",
            description = "Agrégation geohash_grid ou liste individuelle selon le niveau de zoom. Zoom ≥ 14 → individus."
    )
    @GetMapping("/clusters")
    public List<MapPointView> getClusters(
            @RequestParam int zoomLevel,
            @RequestParam(required = false) UUID jobId,
            @RequestParam(required = false) UUID sectorId,
            @RequestParam(required = false) Double minLat,
            @RequestParam(required = false) Double maxLat,
            @RequestParam(required = false) Double minLon,
            @RequestParam(required = false) Double maxLon
    ) {
        return userMapService.getClusters(zoomLevel, jobId, sectorId, minLat, maxLat, minLon, maxLon);
    }

    @Operation(
            summary = "Utilisateurs d'un cluster geohash",
            description = "Retourne jusqu'à 100 utilisateurs appartenant à la cellule geohash cliquée."
    )
    @GetMapping("/cluster-users")
    public List<UserSearchResult> getClusterUsers(
            @RequestParam String geohashCell,
            @RequestParam(required = false) UUID jobId,
            @RequestParam(required = false) UUID sectorId
    ) {
        return userMapService.getClusterUsers(geohashCell, jobId, sectorId);
    }
}
