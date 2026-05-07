package io.agentgrid.coworksimple.booking.adapter.in.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingDTO(
        UUID id,
        UUID roomId,
        String userEmail,
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {
}
