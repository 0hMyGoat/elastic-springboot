package fr.octocorn.elasticspringboot.infrastructure.config;

import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(UserDocument.class);
        if (!indexOps.exists()) {
            indexOps.create();          // Crée l'index
            indexOps.putMapping();      // Applique le mapping de UserDocument
        }
    }
}