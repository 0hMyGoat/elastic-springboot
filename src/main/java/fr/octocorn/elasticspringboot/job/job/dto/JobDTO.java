package fr.octocorn.elasticspringboot.job.job.dto;

import java.util.UUID;

public record JobDTO(
        UUID id,
        String code,
        String name,
        UUID sectorId,
        String sectorName
) {}

