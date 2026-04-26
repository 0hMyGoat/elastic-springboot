package fr.octocorn.elasticspringboot.infrastructure.datagen;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("datagen")
@RequiredArgsConstructor
public class DataGenerator implements ApplicationRunner {

    private final SectorDataGenerator sectorDataGenerator;
    private final JobDataGenerator    jobDataGenerator;
    private final UserDataGenerator   userDataGenerator;

    @Override
    public void run(ApplicationArguments args) {
        log.info("========== START DATA GENERATION ==========");
        sectorDataGenerator.generate();
        jobDataGenerator.generate();
        userDataGenerator.generate();
        log.info("========== END DATA GENERATION ==========");
    }
}

