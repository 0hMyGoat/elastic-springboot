package fr.octocorn.elasticspringboot.infrastructure.elasticsearch;

/**
 * Contrat à implémenter par chaque domaine souhaitant gérer un index Elasticsearch.
 * Chaque implémentation doit être déclarée comme bean Spring (@Component).
 */
public interface ElasticsearchIndex {

    /**
     * Retourne la classe du document Elasticsearch associé à cet index.
     */
    Class<?> getDocumentClass();

    /**
     * Retourne le nom de l'index Elasticsearch.
     */
    String getIndexName();
}

