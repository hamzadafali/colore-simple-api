package io.agentgrid.coworksimple.booking.application.port;

import io.agentgrid.coworksimple.booking.domain.Booking;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepositoryPort {

    List<Booking> findAll();

    // Cherche une reservation precise par son identifiant.
    Optional<Booking> findById(UUID id);

    List<Booking> findOverlappingBookings(UUID roomId, OffsetDateTime startTime, OffsetDateTime endTime);

    // Retourne les reservations futures confirmees pour une room donnee.
    List<Booking> findFutureBookingsByRoomId(UUID roomId);

    Booking save(Booking booking);
}
