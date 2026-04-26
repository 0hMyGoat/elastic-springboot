package fr.octocorn.elasticspringboot.user.infrastructure.amqp;


import fr.octocorn.elasticspringboot.infrastructure.config.RabbitMQConfig;
import fr.octocorn.elasticspringboot.user.domain.event.UserSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**     * Publie un événement d'indexation après commit de la transaction.     *     * @param event événement contenant l'identifiant de l'utilisateur sauvegardé     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserSaved(UserSavedEvent event) {
        log.debug("[UserEventPublisher] Publication indexation pour {}.", event.userId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EXCHANGE, RabbitMQConfig.ROUTING_INDEX, event.userId());
    }

    /**     * Publie un événement de suppression après commit de la transaction.     *     * @param userId identifiant de l'utilisateur supprimé     */
    public void publishDelete(UUID userId) {
        log.debug("[UserEventPublisher] Publication suppression pour {}.", userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EXCHANGE, RabbitMQConfig.ROUTING_DELETE, userId);
    }
}