package fr.octocorn.elasticspringboot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * Configuration pour les tests avec Elasticsearch
 *
 * Assurez-vous que Elasticsearch est en cours d'exécution sur localhost:9200
 */
@TestConfiguration
public class ElasticsearchTestConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .withConnectTimeout(5000)
                .withSocketTimeout(60000)
                .build();
    }
}

