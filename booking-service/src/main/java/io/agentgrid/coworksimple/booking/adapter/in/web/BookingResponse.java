package io.agentgrid.coworksimple.booking.adapter.in.web;

import io.agentgrid.coworksimple.booking.domain.Booking;
import io.agentgrid.coworksimple.booking.domain.BookingStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID roomId,
        String userEmail,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        // Statut exposé dans la réponse REST après création d'une réservation.
        BookingStatus status
) {

    static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoomId(),
                booking.getUserEmail(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus()
        );
    }
}
