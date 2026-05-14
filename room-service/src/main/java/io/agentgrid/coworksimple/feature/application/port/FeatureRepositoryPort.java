package io.agentgrid.coworksimple.feature.application.port;

import io.agentgrid.coworksimple.feature.domain.Feature;
import java.util.Optional;
import java.util.UUID;

public interface FeatureRepositoryPort {

    boolean existsByLabelIgnoreCase(String label);

    Feature save(Feature feature);

    Optional<Feature> findById(UUID id);
}

