package fr.octocorn.elasticspringboot.user.application.view;

import java.time.LocalDate;
import java.util.UUID;

public record UserJobView(
        UUID id,
        UUID jobId,
        String jobName,
        String sectorName,
        Boolean primaryJob,
        LocalDate startDate,
        LocalDate endDate
) {}

