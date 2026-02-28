package io.agentgrid.coworksimple.room;

public record Room(
        Long id,
        String name,
        int capacity,
        boolean isAvailable
) {
}
