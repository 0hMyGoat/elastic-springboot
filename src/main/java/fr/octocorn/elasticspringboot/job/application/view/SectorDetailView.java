package fr.octocorn.elasticspringboot.job.application.view;

import java.util.List;
import java.util.UUID;

public record SectorDetailView(
        UUID id,
        String code,
        String name,
        List<JobView> jobs
) {}

