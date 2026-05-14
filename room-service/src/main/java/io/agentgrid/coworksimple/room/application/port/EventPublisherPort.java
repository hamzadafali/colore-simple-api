package io.agentgrid.coworksimple.room.application.port;

import io.agentgrid.coworksimple.room.domain.event.RoomDeletedEvent;

public interface EventPublisherPort {

    // Port applicatif : RoomService publie un evenement sans connaitre RabbitMQ.
    void publish(RoomDeletedEvent event);
}
