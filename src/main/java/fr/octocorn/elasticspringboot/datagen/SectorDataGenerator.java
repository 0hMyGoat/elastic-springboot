package fr.octocorn.elasticspringboot.datagen;

import fr.octocorn.elasticspringboot.job.domain.sector.Sector;
import fr.octocorn.elasticspringboot.job.domain.sector.SectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Profile("datagen")
@RequiredArgsConstructor
public class SectorDataGenerator {

    private final SectorRepository sectorRepository;

    public void generate() {
        if (sectorRepository.count() > 0) {
            log.info("[SectorDataGenerator] Sectors already exist, skipping.");
            return;
        }

        List<Sector> sectors = Arrays.stream(Sectors.values())
                .map(s -> Sector.builder()
                        .code(s.name())
                        .name(s.getLabel())
                        .build())
                .toList();

        sectorRepository.saveAll(sectors);
        log.info("[SectorDataGenerator] {} sectors created.", sectors.size());
    }
}
