package io.agentgrid.coworksimple.room;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getRooms() {
        List<RoomDTO> rooms = roomService.findAll().stream()
                .map(RoomMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(rooms);
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        Room room = RoomMapper.toRoom(roomDTO);
        Optional<Room> created = roomService.create(room);

        if (created.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        RoomDTO response = RoomMapper.toDto(created.get());
        URI location = URI.create("/api/v1/rooms/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable UUID id, @Valid @RequestBody RoomDTO roomDTO) {
        Room room = new Room(id, roomDTO.name(), roomDTO.capacity());

        try {
            Optional<Room> updated = roomService.update(id, room);
            if (updated.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(RoomMapper.toDto(updated.get()));
        } catch (RoomService.RoomNameConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
