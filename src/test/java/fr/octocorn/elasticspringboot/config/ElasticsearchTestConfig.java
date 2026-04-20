package fr.octocorn.elasticspringboot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * Configuration Elasticsearch pour les tests.
 * Requiert une instance Elasticsearch accessible sur localhost:9200.
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

