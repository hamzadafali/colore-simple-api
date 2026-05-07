package io.agentgrid.coworksimple.booking.application.port;

import io.agentgrid.coworksimple.booking.domain.Booking;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepositoryPort {

    List<Booking> findAll();

    List<Booking> findOverlappingBookings(UUID roomId, OffsetDateTime startTime, OffsetDateTime endTime);

    Booking save(Booking booking);
}
