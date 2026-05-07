package io.agentgrid.coworksimple.room.adapter.out.persistence;

import io.agentgrid.coworksimple.room.domain.Room;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<Room, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Room> findByNameIgnoreCase(String name);
}
