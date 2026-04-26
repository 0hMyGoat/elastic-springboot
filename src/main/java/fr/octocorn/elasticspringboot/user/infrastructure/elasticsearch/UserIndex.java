package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;

import fr.octocorn.elasticspringboot.infrastructure.elasticsearch.ElasticsearchIndex;
import org.springframework.stereotype.Component;

/**
 * Déclaration de l'index Elasticsearch du domaine utilisateur.
 */
@Component
public class UserIndex implements ElasticsearchIndex {

    @Override
    public Class<?> getDocumentClass() {
        return UserDocument.class;
    }

    @Override
    public String getIndexName() {
        return "users";
    }
}

