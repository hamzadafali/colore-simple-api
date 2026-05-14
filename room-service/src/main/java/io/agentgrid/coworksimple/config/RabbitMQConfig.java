package io.agentgrid.coworksimple.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nom de l'exchange utilise par room-service pour publier tous les evenements lies aux rooms.
    public static final String ROOM_EVENTS_EXCHANGE = "room.events";

    // Routing key precise pour l'evenement de suppression d'une room.
    public static final String ROOM_DELETED_ROUTING_KEY = "room.deleted";

    @Bean
    public TopicExchange roomEventsExchange() {
        // Exchange durable : RabbitMQ le garde meme si le broker redemarre.
        return new TopicExchange(ROOM_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        // Convertit les records Java en JSON pour eviter un format binaire specifique a Java.
        return new Jackson2JsonMessageConverter();
    }
}
