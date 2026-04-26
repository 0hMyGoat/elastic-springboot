package fr.octocorn.elasticspringboot.job.application.view;

import java.util.UUID;

public record SectorView(
        UUID id,
        String code,
        String name
) {}

