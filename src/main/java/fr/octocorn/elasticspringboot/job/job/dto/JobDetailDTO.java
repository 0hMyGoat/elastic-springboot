package fr.octocorn.elasticspringboot.job.job.dto;

import fr.octocorn.elasticspringboot.job.sector.dto.SectorDTO;

import java.util.UUID;

public record JobDetailDTO(
        UUID id,
        String code,
        String name,
        SectorDTO sector
) {}

