package fr.octocorn.elasticspringboot.job.job.exception;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(UUID id) {
        super("Job not found with id: " + id);
    }
}

