package io.agentgrid.coworksimple.room;

final class RoomMapper {

    private RoomMapper() {
    }

    static RoomDTO toDto(Room room) {
        return new RoomDTO(
                room.id(),
                room.name(),
                room.capacity(),
                room.isAvailable()
        );
    }

    static Room toRoom(RoomDTO dto) {
        return new Room(
                dto.id(),
                dto.name(),
                dto.capacity(),
                dto.isAvailable()
        );
    }
}
