package io.agentgrid.coworksimple.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingRequest(
        @NotNull UUID roomId,
        @NotBlank String userEmail,
        @Future @NotNull OffsetDateTime startTime,
        @Future @NotNull OffsetDateTime endTime
) {
}
