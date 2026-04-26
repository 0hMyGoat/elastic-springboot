package fr.octocorn.elasticspringboot.infrastructure.api;


import fr.octocorn.elasticspringboot.infrastructure.elasticsearch.IndexManager;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserIndexer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "Opérations techniques et de maintenance")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserIndexer userIndexer;
    private final IndexManager indexManager;

    /**
     * Synchronise tous les utilisateurs existants vers Elasticsearch.
     */
    @Operation(summary = "Synchroniser les utilisateurs vers Elasticsearch")
    @ApiResponse(responseCode = "202", description = "Synchronisation lancée")
    @PostMapping("/users/sync")
    public ResponseEntity<Void> synchroniserUtilisateurs() {
        userIndexer.synchronizeAll();
        return ResponseEntity.accepted().build();
    }

    /**
     * Supprime et recrée un index Elasticsearch avec le mapping à jour.
     *
     * @param indexName nom de l'index à recréer (ex: users)
     */
    @Operation(summary = "Recréer un index Elasticsearch par nom")
    @ApiResponse(responseCode = "202", description = "Recréation lancée")
    @PostMapping("/{indexName}/index/recreate")
    public ResponseEntity<Void> recreerIndex(@PathVariable String indexName) {
        indexManager.recreerIndex(indexName);
        return ResponseEntity.accepted().build();
    }

    /**
     * Supprime et recrée tous les index Elasticsearch enregistrés.
     */
    @Operation(summary = "Recréer tous les index Elasticsearch")
    @ApiResponse(responseCode = "202", description = "Recréation lancée")
    @PostMapping("/index/recreate")
    public ResponseEntity<Void> recreerTousLesIndex() {
        indexManager.recreerTousLesIndex();
        return ResponseEntity.accepted().build();
    }
}