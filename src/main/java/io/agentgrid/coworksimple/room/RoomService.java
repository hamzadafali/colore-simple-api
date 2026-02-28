package io.agentgrid.coworksimple.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final ConcurrentMap<Long, Room> rooms = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> roomNames = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0L);

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    public Optional<Room> findById(Long id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public Optional<Room> create(Room room) {
        String normalizedName = normalizeName(room.name());
        if (normalizedName == null) {
            throw new IllegalArgumentException("Room name must be provided");
        }

        Long newId = idSequence.incrementAndGet();
        Room toStore = new Room(newId, room.name().trim(), room.capacity(), room.isAvailable());

        Long existing = roomNames.putIfAbsent(normalizedName, newId);
        if (existing != null) {
            return Optional.empty();
        }

        rooms.put(newId, toStore);
        return Optional.of(toStore);
    }

    public Optional<Room> update(Long id, Room room) {
        String normalizedName = normalizeName(room.name());
        if (normalizedName == null) {
            throw new IllegalArgumentException("Room name must be provided");
        }

        Room updated = rooms.compute(id, (key, existing) -> {
            if (existing == null) {
                return null;
            }

            String existingNormalized = normalizeName(existing.name());
            if (!existingNormalized.equals(normalizedName)) {
                Long conflictId = roomNames.putIfAbsent(normalizedName, key);
                if (conflictId != null && !conflictId.equals(key)) {
                    throw new RoomNameConflictException();
                }
                roomNames.remove(existingNormalized, key);
            }

            return new Room(key, room.name().trim(), room.capacity(), room.isAvailable());
        });
        return Optional.ofNullable(updated);
    }

    public Optional<Room> updateAvailability(Long id, boolean isAvailable) {
        Room updated = rooms.computeIfPresent(id, (key, existing) ->
                new Room(key, existing.name(), existing.capacity(), isAvailable)
        );
        return Optional.ofNullable(updated);
    }

    public void delete(Long id) {
        Room removed = rooms.remove(id);
        if (removed != null) {
            roomNames.remove(normalizeName(removed.name()), id);
        }
    }

    private static String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return name.trim().toLowerCase(Locale.ROOT);
    }

    static class RoomNameConflictException extends RuntimeException {
    }
}
