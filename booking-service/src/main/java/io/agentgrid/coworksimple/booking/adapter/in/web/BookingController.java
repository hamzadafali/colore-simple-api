package io.agentgrid.coworksimple.booking.adapter.in.web;

import io.agentgrid.coworksimple.booking.application.BookingRequest;
import io.agentgrid.coworksimple.booking.application.BookingService;
import io.agentgrid.coworksimple.booking.domain.Booking;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getBookings() {
        List<BookingDTO> bookings = bookingService.findAll().stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(bookings);
    }
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        BookingResponse response = BookingResponse.from(booking);
        URI location = URI.create("/api/v1/bookings/" + response.id());

        return ResponseEntity.created(location).body(response);
    }
}
