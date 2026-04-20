package fr.octocorn.elasticspringboot.job.sector.exception;

import java.util.UUID;

public class SectorNotFoundException extends RuntimeException {
    public SectorNotFoundException(UUID id) {
        super("Sector not found with id: " + id);
    }
}

