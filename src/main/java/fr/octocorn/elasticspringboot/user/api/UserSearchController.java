package fr.octocorn.elasticspringboot.user.api;
import fr.octocorn.elasticspringboot.user.application.UserSearchService;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchCriteria;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Rechercher des utilisateurs")
    @GetMapping("/search")
    public Page<UserSearchResult> search(@Valid @ParameterObject UserSearchCriteria criteria) {
        return userSearchService.search(criteria);
    }
}
