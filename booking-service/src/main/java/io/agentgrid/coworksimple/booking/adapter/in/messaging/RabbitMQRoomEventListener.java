package io.agentgrid.coworksimple.booking.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.agentgrid.coworksimple.booking.application.BookingService;
import io.agentgrid.coworksimple.booking.domain.event.RoomDeletedEvent;
import io.agentgrid.coworksimple.config.RabbitMQConfig;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQRoomEventListener {

    private final BookingService bookingService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.ROOM_DELETED_QUEUE)
    public void onRoomDeleted(Message message) throws IOException {
        // Le message arrive en JSON depuis room-service.
        RoomDeletedEvent event = objectMapper.readValue(message.getBody(), RoomDeletedEvent.class);

        // Log d'entree pour verifier que booking-service a bien consomme l'evenement.
        log.info("[EVENT] Received room.deleted event for room {}", event.roomId());

        // La regle metier reste dans BookingService, pas dans l'adapter RabbitMQ.
        bookingService.cancelFutureBookingsForRoom(event.roomId());
    }
}
