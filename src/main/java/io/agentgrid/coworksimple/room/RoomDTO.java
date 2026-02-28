package io.agentgrid.coworksimple.room;

public record RoomDTO(
        Long id,
        String name,
        int capacity,
        boolean isAvailable
) {
}
