package io.agentgrid.coworksimple.feature;

import io.agentgrid.coworksimple.room.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "features")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feature {

    @Id
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String label;

    @ManyToMany(mappedBy = "features")
    private Set<Room> rooms = new LinkedHashSet<>();

    public Feature(UUID id, String label) {
        this.id = id;
        this.label = label;
    }
}
