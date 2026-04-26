package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, UUID> {}