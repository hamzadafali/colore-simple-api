package io.agentgrid.coworksimple.booking.application;

import io.agentgrid.coworksimple.booking.application.port.BookingRepositoryPort;
import io.agentgrid.coworksimple.booking.domain.Booking;
import io.agentgrid.coworksimple.room.application.port.RoomRepositoryPort;
import io.agentgrid.coworksimple.room.domain.Room;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepositoryPort bookingRepository;
    private final RoomRepositoryPort roomRepository;

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        OffsetDateTime startTime = request.startTime();
        OffsetDateTime endTime = request.endTime();
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new RoomNotFoundException(request.roomId()));

        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                room.getId(), startTime, endTime
        );

        if (!conflicts.isEmpty()) {
            Booking firstConflict = conflicts.getFirst();
            throw new BookingConflictException(firstConflict.getId());
        }

        Booking booking = new Booking(
                UUID.randomUUID(),
                room,
                request.userEmail(),
                startTime,
                endTime
        );

        return bookingRepository.save(booking);
    }

    public static class RoomNotFoundException extends RuntimeException {
        public RoomNotFoundException(UUID roomId) {
            super("Room not found: " + roomId);
        }
    }

    public static class BookingConflictException extends RuntimeException {
        public BookingConflictException(UUID bookingId) {
            super("Room already booked, conflict with booking: " + bookingId);
        }
    }
}
