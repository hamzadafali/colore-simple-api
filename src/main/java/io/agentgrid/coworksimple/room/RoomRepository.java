package io.agentgrid.coworksimple.room;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Room> findByNameIgnoreCase(String name);
}
