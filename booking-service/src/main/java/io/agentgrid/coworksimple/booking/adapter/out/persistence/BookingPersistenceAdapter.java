package io.agentgrid.coworksimple.booking.adapter.out.persistence;

import io.agentgrid.coworksimple.booking.application.port.BookingRepositoryPort;
import io.agentgrid.coworksimple.booking.domain.Booking;
import java.time.OffsetDateTime;
import java.util.List;
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
    public List<Booking> findOverlappingBookings(UUID roomId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return bookingJpaRepository.findOverlappingBookings(roomId, startTime, endTime);
    }

    @Override
    public Booking save(Booking booking) {
        return bookingJpaRepository.save(booking);
    }
}
