package io.agentgrid.coworksimple.room.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record RoomDTO(
        UUID id,
        @NotBlank String name,
        @Min(1) int capacity
) {
}
