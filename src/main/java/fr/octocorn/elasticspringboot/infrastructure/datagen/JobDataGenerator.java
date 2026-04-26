package fr.octocorn.elasticspringboot.infrastructure.datagen;

import fr.octocorn.elasticspringboot.job.domain.job.Job;
import fr.octocorn.elasticspringboot.job.domain.job.JobRepository;
import fr.octocorn.elasticspringboot.job.domain.sector.Sector;
import fr.octocorn.elasticspringboot.job.domain.sector.SectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("datagen")
@RequiredArgsConstructor
public class JobDataGenerator {

    private final JobRepository jobRepository;
    private final SectorRepository sectorRepository;

    public void generate() {
        if (jobRepository.count() > 0) {
            log.info("[JobDataGenerator] Jobs already exist, skipping.");
            return;
        }

        Map<String, Sector> sectorByCode = sectorRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Sector::getCode, s -> s));

        if (sectorByCode.isEmpty()) {
            log.error("[JobDataGenerator] No sectors found — run SectorDataGenerator first.");
            return;
        }

        List<Job> jobs = Arrays.stream(Jobs.values())
                .map(j -> Job.builder()
                        .code(j.name())
                        .name(j.getLabel())
                        .sector(sectorByCode.get(j.getSector().name()))
                        .build())
                .toList();

        jobRepository.saveAll(jobs);
        log.info("[JobDataGenerator] {} jobs created across {} sectors.", jobs.size(), sectorByCode.size());
    }
}

