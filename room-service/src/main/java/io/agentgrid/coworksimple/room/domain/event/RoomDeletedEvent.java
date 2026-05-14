package io.agentgrid.coworksimple.room.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

// Evenement publie par room-service quand une salle est supprimee.
public record RoomDeletedEvent(
        UUID roomId,
        OffsetDateTime timestamp
) {
}
