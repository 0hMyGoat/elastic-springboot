package fr.octocorn.elasticspringboot.job.domain.exception;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException {

    /**
     * Crée une exception pour un métier introuvable.
     *
     * @param id identifiant du métier introuvable
     */
    public JobNotFoundException(UUID id) {
        super("Métier introuvable avec l'identifiant : " + id);
    }
}

