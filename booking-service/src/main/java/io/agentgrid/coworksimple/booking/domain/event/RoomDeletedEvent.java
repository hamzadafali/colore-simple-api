package io.agentgrid.coworksimple.booking.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

// Copie locale de l'evenement recu par booking-service, sans module shared pour l'instant.
public record RoomDeletedEvent(
        UUID roomId,
        OffsetDateTime timestamp
) {
}
