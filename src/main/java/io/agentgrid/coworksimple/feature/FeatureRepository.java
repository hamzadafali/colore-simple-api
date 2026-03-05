package io.agentgrid.coworksimple.feature;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, UUID> {

    boolean existsByLabelIgnoreCase(String label);
}
