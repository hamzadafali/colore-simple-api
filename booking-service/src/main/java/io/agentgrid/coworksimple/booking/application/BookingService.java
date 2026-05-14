package io.agentgrid.coworksimple.booking.application;

import io.agentgrid.coworksimple.booking.application.port.BookingRepositoryPort;
import io.agentgrid.coworksimple.booking.application.port.RoomClientPort;
import io.agentgrid.coworksimple.booking.domain.Booking;
import io.agentgrid.coworksimple.booking.domain.BookingStatus;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepositoryPort bookingRepository;
    private final RoomClientPort roomClient;

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Booking findById(UUID id) {
        // Le service transforme l'absence en erreur metier claire pour le controller.
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        OffsetDateTime startTime = request.startTime();
        OffsetDateTime endTime = request.endTime();
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (!roomClient.existsById(request.roomId())) {
            throw new RoomNotFoundException(request.roomId());
        }

        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                request.roomId(), startTime, endTime
        );

        if (!conflicts.isEmpty()) {
            Booking firstConflict = conflicts.getFirst();
            throw new BookingConflictException(firstConflict.getId());
        }

        Booking booking = new Booking(
                UUID.randomUUID(),
                request.roomId(),
                request.userEmail(),
                startTime,
                endTime
        );

        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelFutureBookingsForRoom(UUID roomId) {
        // On ne charge que les reservations futures encore confirmees pour cette room.
        List<Booking> futureBookings = bookingRepository.findFutureBookingsByRoomId(roomId);

        for (Booking booking : futureBookings) {
            // Chaque reservation concernee passe en CANCELLED apres l'evenement room.deleted.
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }

        // Trace explicite pour suivre le traitement asynchrone des evenements.
        log.info("[EVENT] Cancelled {} future booking(s) for deleted room {}", futureBookings.size(), roomId);
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

    public static class BookingNotFoundException extends RuntimeException {
        public BookingNotFoundException(UUID bookingId) {
            super("Booking not found: " + bookingId);
        }
    }
}
