package io.agentgrid.coworksimple.booking.application.port;

import java.util.UUID;

public interface RoomClientPort {

    boolean existsById(UUID roomId);
}
