package fr.octocorn.elasticspringboot.job.sector.dto;

import fr.octocorn.elasticspringboot.job.job.dto.JobDTO;

import java.util.List;
import java.util.UUID;

public record SectorDetailDTO(
        UUID id,
        String code,
        String name,
        List<JobDTO> jobs
) {}

