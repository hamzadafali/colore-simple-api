package io.agentgrid.coworksimple.booking.adapter.out.persistence;

import io.agentgrid.coworksimple.booking.application.port.BookingRepositoryPort;
import io.agentgrid.coworksimple.booking.domain.Booking;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingPersistenceAdapter implements BookingRepositoryPort {

    private final BookingJpaRepository bookingJpaRepository;

    @Override
    public List<Booking> findAll() {
        return bookingJpaRepository.findAll();
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        // Delegue la recherche par ID au repository Spring Data.
        return bookingJpaRepository.findById(id);
    }

    @Override
    public List<Booking> findOverlappingBookings(UUID roomId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return bookingJpaRepository.findOverlappingBookings(roomId, startTime, endTime);
    }

    @Override
    public List<Booking> findFutureBookingsByRoomId(UUID roomId) {
        // L'adapter traduit le port hexagonal vers la requete Spring Data JPA.
        return bookingJpaRepository.findFutureBookingsByRoomId(roomId);
    }

    @Override
    public Booking save(Booking booking) {
        return bookingJpaRepository.save(booking);
    }
}
