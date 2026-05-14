package io.agentgrid.coworksimple.room.adapter.out.persistence;

import io.agentgrid.coworksimple.room.application.port.RoomRepositoryPort;
import io.agentgrid.coworksimple.room.domain.Room;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomPersistenceAdapter implements RoomRepositoryPort {

    private final RoomJpaRepository roomJpaRepository;

    @Override
    public List<Room> findAll() {
        return roomJpaRepository.findAll();
    }

    @Override
    public Optional<Room> findById(UUID id) {
        return roomJpaRepository.findById(id);
    }

    @Override
    public Room save(Room room) {
        return roomJpaRepository.save(room);
    }

    @Override
    public boolean existsById(UUID id) {
        return roomJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        roomJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return roomJpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Optional<Room> findByNameIgnoreCase(String name) {
        return roomJpaRepository.findByNameIgnoreCase(name);
    }
}

