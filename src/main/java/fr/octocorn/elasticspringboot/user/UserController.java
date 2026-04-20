package fr.octocorn.elasticspringboot.user;

import fr.octocorn.elasticspringboot.user.dto.UserCreateDTO;
import fr.octocorn.elasticspringboot.user.dto.UserDTO;
import fr.octocorn.elasticspringboot.user.dto.UserUpdateDTO;
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
    public Page<UserDTO> findAll(@ParameterObject Pageable pageable) {
        return userService.findAll(pageable);
    }

    @Operation(summary = "Récupérer un utilisateur par son identifiant")
    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @Operation(summary = "Créer un utilisateur")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données de création invalides")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {
        return userService.create(dto);
    }

    @Operation(summary = "Mettre à jour un utilisateur")
    @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides")
    @PutMapping("/{id}")
    public UserDTO update(@PathVariable UUID id, @Valid @RequestBody UserUpdateDTO dto) {
        return userService.update(id, dto);
    }

    @Operation(summary = "Supprimer un utilisateur")
    @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

