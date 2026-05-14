package io.agentgrid.coworksimple.room.adapter.out.messaging;

import io.agentgrid.coworksimple.config.RabbitMQConfig;
import io.agentgrid.coworksimple.room.application.port.EventPublisherPort;
import io.agentgrid.coworksimple.room.domain.event.RoomDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventAdapter implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(RoomDeletedEvent event) {
        // Envoie l'evenement dans l'exchange room.events avec la routing key room.deleted.
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ROOM_EVENTS_EXCHANGE,
                RabbitMQConfig.ROOM_DELETED_ROUTING_KEY,
                event
        );
    }
}
