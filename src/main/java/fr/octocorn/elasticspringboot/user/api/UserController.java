package fr.octocorn.elasticspringboot.user.api;

import fr.octocorn.elasticspringboot.user.application.UserService;
import fr.octocorn.elasticspringboot.user.application.command.CreateUserCommand;
import fr.octocorn.elasticspringboot.user.application.command.UpdateUserCommand;
import fr.octocorn.elasticspringboot.user.application.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Users", description = "Gestion des utilisateurs")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Lister les utilisateurs", description = "Retourne la liste paginée des utilisateurs.")
    @GetMapping
    public Page<UserView> findAll(@ParameterObject Pageable pageable) {
        return userService.findAll(pageable);
    }

    @Operation(summary = "Récupérer un utilisateur par son identifiant")
    @GetMapping("/{id}")
    public UserView findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @Operation(summary = "Créer un utilisateur")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données de création invalides")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserView create(@Valid @RequestBody CreateUserCommand command) {
        return userService.create(command);
    }

    @Operation(summary = "Mettre à jour un utilisateur")
    @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides")
    @PutMapping("/{id}")
    public UserView update(@PathVariable UUID id, @Valid @RequestBody UpdateUserCommand command) {
        return userService.update(id, command);
    }

    @Operation(summary = "Supprimer un utilisateur")
    @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

