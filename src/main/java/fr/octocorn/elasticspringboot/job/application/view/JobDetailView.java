package fr.octocorn.elasticspringboot.job.application.view;

import java.util.UUID;

public record JobDetailView(
        UUID id,
        String code,
        String name,
        SectorView sector
) {}

