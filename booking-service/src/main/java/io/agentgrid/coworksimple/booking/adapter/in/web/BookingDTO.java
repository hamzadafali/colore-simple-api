package io.agentgrid.coworksimple.booking.adapter.in.web;

import io.agentgrid.coworksimple.booking.domain.BookingStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingDTO(
        UUID id,
        UUID roomId,
        String userEmail,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        // Statut exposé dans la réponse de liste des réservations.
        BookingStatus status
) {
}
