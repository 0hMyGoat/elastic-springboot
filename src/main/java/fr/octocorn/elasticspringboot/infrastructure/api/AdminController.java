package fr.octocorn.elasticspringboot.infrastructure.api;


import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserIndexer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "Opérations techniques et de maintenance")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserIndexer userIndexer;

    @Operation(summary = "Synchroniser les utilisateurs vers Elasticsearch")
    @PostMapping("/users/sync")
    public ResponseEntity<Void> synchroniserUtilisateurs() {
        userIndexer.synchronizeAll();
        return ResponseEntity.accepted().build();
    }
}