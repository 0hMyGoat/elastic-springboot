package fr.octocorn.elasticspringboot.infrastructure.elasticsearch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexManager {

    private final ElasticsearchOperations elasticsearchOperations;
    private final List<ElasticsearchIndex> indexes;

    /**
     * Crée tous les index absents au démarrage de l'application.
     */
    @PostConstruct
    public void initialiserSiAbsent() {
        indexes.forEach(index -> {
            IndexOperations indexOps = elasticsearchOperations.indexOps(index.getDocumentClass());
            if (!indexOps.exists()) {
                indexOps.createWithMapping();
                log.info("[IndexManager] Index '{}' créé.", index.getIndexName());
            }
        });
    }

    /**
     * Supprime puis recrée un index par son nom avec le mapping à jour.
     *
     * @param indexName nom de l'index à recréer
     */
    public void recreerIndex(String indexName) {
        indexes.stream()
                .filter(i -> i.getIndexName().equals(indexName))
                .findFirst()
                .ifPresentOrElse(
                        index -> {
                            IndexOperations indexOps = elasticsearchOperations.indexOps(index.getDocumentClass());
                            if (indexOps.exists()) {
                                indexOps.delete();
                                log.info("[IndexManager] Index '{}' supprimé.", indexName);
                            }
                            indexOps.createWithMapping();
                            log.info("[IndexManager] Index '{}' recréé avec le mapping à jour.", indexName);
                        },
                        () -> log.warn("[IndexManager] Index '{}' introuvable, recréation ignorée.", indexName)
                );
    }

    /**
     * Supprime puis recrée tous les index enregistrés.
     */
    public void recreerTousLesIndex() {
        indexes.forEach(index -> recreerIndex(index.getIndexName()));
    }
}