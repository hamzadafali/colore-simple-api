package io.agentgrid.coworksimple.booking.adapter.in.web;

import io.agentgrid.coworksimple.booking.domain.Booking;

final class BookingMapper {

    private BookingMapper() {
    }

    static BookingDTO toDto(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getUserEmail(),
                booking.getStartTime(),
                booking.getEndTime()
        );
    }
}
