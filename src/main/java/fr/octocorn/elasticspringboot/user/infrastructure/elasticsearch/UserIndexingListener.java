package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;


import fr.octocorn.elasticspringboot.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserIndexingListener {

    private final UserIndexer userIndexer;

    /**     * Consomme les événements d'indexation et indexe l'utilisateur dans Elasticsearch.     *     * @param userId identifiant de l'utilisateur à indexer     */
    @RabbitListener(queues = RabbitMQConfig.USER_INDEX_QUEUE)
    public void onIndex(UUID userId) {
        log.debug("[UserIndexingListener] Message d'indexation reçu pour {}.", userId);
        userIndexer.index(userId);
    }

    /**     * Consomme les événements de suppression et retire l'utilisateur de l'index.     *     * @param userId identifiant de l'utilisateur à supprimer     */
    @RabbitListener(queues = RabbitMQConfig.USER_DELETE_QUEUE)
    public void onDelete(UUID userId) {
        log.debug("[UserIndexingListener] Message de suppression reçu pour {}.", userId);
        userIndexer.delete(userId);
    }
}