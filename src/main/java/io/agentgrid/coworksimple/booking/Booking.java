package io.agentgrid.coworksimple.booking;

import io.agentgrid.coworksimple.room.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotBlank
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Future
    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Future
    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    public Booking(UUID id, Room room, String userEmail, OffsetDateTime startTime, OffsetDateTime endTime) {
        this.id = id;
        this.room = room;
        this.userEmail = userEmail;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
