package io.agentgrid.coworksimple.feature.adapter.out.persistence;

import io.agentgrid.coworksimple.feature.domain.Feature;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureJpaRepository extends JpaRepository<Feature, UUID> {

    boolean existsByLabelIgnoreCase(String label);
}
