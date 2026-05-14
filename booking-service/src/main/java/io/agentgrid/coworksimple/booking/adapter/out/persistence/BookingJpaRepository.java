package io.agentgrid.coworksimple.booking.adapter.out.persistence;

import io.agentgrid.coworksimple.booking.domain.Booking;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingJpaRepository extends JpaRepository<Booking, UUID> {

    @Query("select b from Booking b where b.roomId = :roomId and b.startTime < :endTime and b.endTime > :startTime")
    List<Booking> findOverlappingBookings(
            @Param("roomId") UUID roomId,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );

    // JPQL portable : current_timestamp represente le NOW() de la base.
    @Query("""
            select b from Booking b
            where b.roomId = :roomId
              and b.startTime > current_timestamp
              and b.status = io.agentgrid.coworksimple.booking.domain.BookingStatus.CONFIRMED
            """)
    List<Booking> findFutureBookingsByRoomId(@Param("roomId") UUID roomId);
}
