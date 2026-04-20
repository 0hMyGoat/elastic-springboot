package fr.octocorn.elasticspringboot.user.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UserJobDTO(
        UUID id,
        UUID jobId,
        String jobName,
        String sectorName,
        Boolean primaryJob,
        LocalDate startDate,
        LocalDate endDate
) {}

