package fr.octocorn.elasticspringboot.job.application.view;

import java.util.UUID;

public record JobView(
        UUID id,
        String code,
        String name,
        UUID sectorId,
        String sectorName
) {}

