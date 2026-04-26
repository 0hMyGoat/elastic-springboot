package fr.octocorn.elasticspringboot.user.api;
import fr.octocorn.elasticspringboot.user.application.UserSearchService;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchCriteria;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "Users", description = "Recherche d''utilisateurs")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserSearchController {
    private final UserSearchService userSearchService;
    /**
     * Recherche des utilisateurs a partir de criteres textuels et metier.
     * Retourne une vue allegee. Pour le detail complet, utiliser GET /users/{id}.
     *
     * @param criteria criteres de recherche
     * @return page des resultats de recherche
     */
    @Operation(
            summary = "Rechercher des utilisateurs",
            description = """
                    Recherche plein-texte avec tolérance aux fautes de frappe (`fuzziness: AUTO`).

                    - `query` : recherche libre sur le prénom et le nom — **prioritaire sur firstName/lastName**
                    - `firstName` / `lastName` : recherche floue sur un champ spécifique (ignorés si `query` est fourni)
                    - `city`, `postalCode` : filtres insensibles à la casse
                    - `jobName`, `sectorName` : filtres exacts

                    Tous les critères sont optionnels et combinables.
                    Si aucun critère n'est fourni, retourne l'ensemble des utilisateurs paginés.
                    Pour le détail complet d'un utilisateur, utiliser `GET /users/{id}`.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Résultats de recherche retournés avec succès")
    @ApiResponse(responseCode = "400", description = "Critères invalides (page < 0, size hors limites)")
    @GetMapping("/search")
    public Page<UserSearchResult> search(@Valid @ParameterObject UserSearchCriteria criteria) {
        return userSearchService.search(criteria);
    }
}
