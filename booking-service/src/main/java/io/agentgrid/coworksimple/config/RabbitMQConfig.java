package io.agentgrid.coworksimple.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Meme exchange que room-service : booking-service s'abonne aux evenements publies dessus.
    public static final String ROOM_EVENTS_EXCHANGE = "room.events";

    // Queue dediee aux suppressions de rooms consommees par booking-service.
    public static final String ROOM_DELETED_QUEUE = "room.deleted.queue";

    // Routing key utilisee par room-service pour signaler qu'une room a ete supprimee.
    public static final String ROOM_DELETED_ROUTING_KEY = "room.deleted";

    @Bean
    public TopicExchange roomEventsExchange() {
        // Exchange durable : il reste declare dans RabbitMQ apres un redemarrage du broker.
        return new TopicExchange(ROOM_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue roomDeletedQueue() {
        // Queue durable : les messages peuvent attendre booking-service meme si le service redemarre.
        return new Queue(ROOM_DELETED_QUEUE, true);
    }

    @Bean
    public Binding roomDeletedBinding(Queue roomDeletedQueue, TopicExchange roomEventsExchange) {
        // Binding : tous les messages envoyes avec "room.deleted" arrivent dans room.deleted.queue.
        return BindingBuilder.bind(roomDeletedQueue)
                .to(roomEventsExchange)
                .with(ROOM_DELETED_ROUTING_KEY);
    }
}
