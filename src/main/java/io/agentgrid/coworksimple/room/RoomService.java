package io.agentgrid.coworksimple.room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Optional<Room> findById(UUID id) {
        return roomRepository.findById(id);
    }

    public Optional<Room> create(Room room) {
        if (roomRepository.existsByNameIgnoreCase(room.getName())) {
            return Optional.empty();
        }

        Room toSave = new Room(UUID.randomUUID(), room.getName(), room.getCapacity());
        return Optional.of(roomRepository.save(toSave));
    }

    public Optional<Room> update(UUID id, Room room) {
        return roomRepository.findById(id).map(existing -> {
            if (!existing.getName().equalsIgnoreCase(room.getName())
                    && roomRepository.existsByNameIgnoreCase(room.getName())) {
                throw new RoomNameConflictException();
            }
            existing.setName(room.getName());
            existing.setCapacity(room.getCapacity());
            return roomRepository.save(existing);
        });
    }

    public void delete(UUID id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        }
    }

    static class RoomNameConflictException extends RuntimeException {
    }
}
