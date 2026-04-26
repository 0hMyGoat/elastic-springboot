package fr.octocorn.elasticspringboot.user.api;

import fr.octocorn.elasticspringboot.user.application.UserSearchService;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchCriteriaRequest;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Recherche d'utilisateurs")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserSearchController {

    private final UserSearchService userSearchService;

    /**
     * Recherche plein-texte des utilisateurs sur le prénom et le nom.
     *
     * @param query texte libre de recherche
     * @param page  numéro de page (0-based)
     * @param size  nombre de résultats par page
     * @return page des résultats de recherche
     */
    @Operation(
            summary = "Rechercher des utilisateurs par texte libre",
            description = """
                    Recherche plein-texte avec tolérance aux fautes de frappe (`fuzziness: AUTO`).
                    Recherche sur le prénom et le nom.
                    Pour une recherche par critères structurés, utiliser `POST /users/search`.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Résultats retournés avec succès")
    @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    @GetMapping("/search")
    public Page<UserSearchResult> rechercherParTexte(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return userSearchService.rechercherParTexte(query, PageRequest.of(page, size));
    }

    /**
     * Recherche des utilisateurs par critères structurés transmis en corps de requête.
     *
     * @param criteria critères de recherche
     * @return page des résultats de recherche
     */
    @Operation(
            summary = "Rechercher des utilisateurs par critères structurés",
            description = """
                    Recherche par critères cumulatifs transmis en JSON.

                    - `firstName` / `lastName` : recherche floue
                    - `city`, `postalCode` : filtres insensibles à la casse
                    - `jobName`, `sectorName` : filtres exacts

                    Tous les champs sont optionnels et combinables.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Résultats retournés avec succès")
    @ApiResponse(responseCode = "400", description = "Critères invalides")
    @PostMapping("/search")
    public Page<UserSearchResult> rechercherParCriteres(@Valid @RequestBody UserSearchCriteriaRequest criteria) {
        return userSearchService.rechercherParCriteres(criteria);
    }
}
