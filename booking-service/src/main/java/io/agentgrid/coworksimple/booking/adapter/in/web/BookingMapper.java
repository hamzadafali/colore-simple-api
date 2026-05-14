package io.agentgrid.coworksimple.booking.adapter.in.web;

import io.agentgrid.coworksimple.booking.domain.Booking;

final class BookingMapper {

    private BookingMapper() {
    }

    static BookingDTO toDto(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getRoomId(),
                booking.getUserEmail(),
                booking.getStartTime(),
                booking.getEndTime(),
                // Le DTO de sortie inclut maintenant le statut courant de la réservation.
                booking.getStatus()
        );
    }
}
