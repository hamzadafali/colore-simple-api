package io.agentgrid.coworksimple.room.application.port;

import io.agentgrid.coworksimple.room.domain.Room;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepositoryPort {

    List<Room> findAll();

    Optional<Room> findById(UUID id);

    Room save(Room room);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    boolean existsByNameIgnoreCase(String name);

    Optional<Room> findByNameIgnoreCase(String name);
}

