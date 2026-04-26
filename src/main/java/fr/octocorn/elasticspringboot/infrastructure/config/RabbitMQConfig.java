package fr.octocorn.elasticspringboot.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange — tous les événements user passent par là
    public static final String USER_EXCHANGE = "user.exchange";

    // Queues
    public static final String USER_INDEX_QUEUE   = "user.index.queue";
    public static final String USER_DELETE_QUEUE  = "user.delete.queue";

    // Routing keys
    public static final String ROUTING_INDEX  = "user.index";
    public static final String ROUTING_DELETE = "user.delete";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE, true, false);
    }

    @Bean
    public Queue userIndexQueue() {
        return QueueBuilder.durable(USER_INDEX_QUEUE).build();
    }

    @Bean
    public Queue userDeleteQueue() {
        return QueueBuilder.durable(USER_DELETE_QUEUE).build();
    }

    @Bean
    public Binding bindingIndex(Queue userIndexQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userIndexQueue).to(userExchange).with(ROUTING_INDEX);
    }

    @Bean
    public Binding bindingDelete(Queue userDeleteQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userDeleteQueue).to(userExchange).with(ROUTING_DELETE);
    }

    // Sérialisation JSON pour les messages
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}