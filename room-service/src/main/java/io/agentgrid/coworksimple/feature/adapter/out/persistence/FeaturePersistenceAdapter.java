package io.agentgrid.coworksimple.feature.adapter.out.persistence;

import io.agentgrid.coworksimple.feature.application.port.FeatureRepositoryPort;
import io.agentgrid.coworksimple.feature.domain.Feature;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeaturePersistenceAdapter implements FeatureRepositoryPort {

    private final FeatureJpaRepository featureJpaRepository;

    @Override
    public boolean existsByLabelIgnoreCase(String label) {
        return featureJpaRepository.existsByLabelIgnoreCase(label);
    }

    @Override
    public Feature save(Feature feature) {
        return featureJpaRepository.save(feature);
    }

    @Override
    public Optional<Feature> findById(UUID id) {
        return featureJpaRepository.findById(id);
    }
}

