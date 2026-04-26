package fr.octocorn.elasticspringboot.user.api;

import fr.octocorn.elasticspringboot.user.application.UserSearchCriteria;
import fr.octocorn.elasticspringboot.user.application.UserSearchService;
import fr.octocorn.elasticspringboot.user.application.view.UserView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Users", description = "Recherche d'utilisateurs")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserSearchController {
    private final UserSearchService userSearchService;

    @GetMapping("/search")
    public Page<UserView> search(@Valid @ParameterObject UserSearchCriteria criteria) {
        return userSearchService.search(criteria);
    }
}
