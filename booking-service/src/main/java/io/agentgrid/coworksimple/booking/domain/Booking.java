package io.agentgrid.coworksimple.booking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Id
    private UUID id;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @NotBlank
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Future
    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Future
    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    // Statut métier de la réservation, stocké en texte pour exposer CONFIRMED ou CANCELLED clairement.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    public Booking(UUID id, UUID roomId, String userEmail, OffsetDateTime startTime, OffsetDateTime endTime) {
        this.id = id;
        this.roomId = roomId;
        this.userEmail = userEmail;
        this.startTime = startTime;
        this.endTime = endTime;
        // Une nouvelle réservation démarre confirmée par défaut.
        this.status = BookingStatus.CONFIRMED;
    }
}
