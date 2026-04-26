package fr.octocorn.elasticspringboot.job.domain.exception;

import java.util.UUID;

public class SectorNotFoundException extends RuntimeException {

    /**
     * Crée une exception pour un secteur introuvable.
     *
     * @param id identifiant du secteur introuvable
     */
    public SectorNotFoundException(UUID id) {
        super("Secteur introuvable avec l'identifiant : " + id);
    }
}

