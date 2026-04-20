package fr.octocorn.elasticspringboot.job.sector.dto;

import java.util.UUID;

public record SectorDTO(
        UUID id,
        String code,
        String name
) {}

