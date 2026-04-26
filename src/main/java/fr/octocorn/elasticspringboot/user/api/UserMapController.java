package fr.octocorn.elasticspringboot.user.api;

import fr.octocorn.elasticspringboot.user.application.MapPrecision;
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

    /**
     * Retourne les points à afficher sur la carte selon le niveau de précision.
     *
     * <p>Précisions disponibles :</p>
     * <ul>
     *   <li>{@code CITY} — un cercle par ville (zoom ≤ 8)</li>
     *   <li>{@code POSTAL_CODE} — un cercle par code postal (zoom 9–12)</li>
     *   <li>{@code INDIVIDUAL} — un point par personne (zoom ≥ 13)</li>
     * </ul>
     *
     * <p>En mode {@code INDIVIDUAL}, les paramètres {@code minLat/maxLat/minLon/maxLon}
     * permettent de restreindre la requête à la bounding box visible.</p>
     */
    @Operation(
            summary = "Points géographiques pour la carte",
            description = "Agrégation (CITY/POSTAL_CODE) ou liste individuelle selon la précision demandée."
    )
    @GetMapping("/clusters")
    public List<MapPointView> getClusters(
            @RequestParam(defaultValue = "CITY") MapPrecision precision,
            @RequestParam(required = false) UUID jobId,
            @RequestParam(required = false) UUID sectorId,
            @RequestParam(required = false) Double minLat,
            @RequestParam(required = false) Double maxLat,
            @RequestParam(required = false) Double minLon,
            @RequestParam(required = false) Double maxLon
    ) {
        return userMapService.getClusters(precision, jobId, sectorId, minLat, maxLat, minLon, maxLon);
    }

    /**
     * Retourne la liste des utilisateurs d'un cluster (clic sur un cercle).
     *
     * @param precision CITY ou POSTAL_CODE
     * @param value     valeur du cluster (nom de ville ou code postal, tel que retourné par /clusters)
     * @param jobId     filtre optionnel par métier
     * @param sectorId  filtre optionnel par secteur
     */
    @Operation(
            summary = "Utilisateurs d'un cluster",
            description = "Retourne jusqu'à 100 utilisateurs appartenant à la ville ou au code postal cliqué."
    )
    @GetMapping("/cluster-users")
    public List<UserSearchResult> getClusterUsers(
            @RequestParam(defaultValue = "CITY") MapPrecision precision,
            @RequestParam String value,
            @RequestParam(required = false) UUID jobId,
            @RequestParam(required = false) UUID sectorId
    ) {
        return userMapService.getClusterUsers(precision, value, jobId, sectorId);
    }
}

