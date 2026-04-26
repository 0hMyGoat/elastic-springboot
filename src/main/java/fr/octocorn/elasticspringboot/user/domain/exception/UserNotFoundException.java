package fr.octocorn.elasticspringboot.user.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    /**
     * Crée une exception pour un utilisateur introuvable.
     *
     * @param id identifiant de l'utilisateur introuvable
     */
    public UserNotFoundException(UUID id) {
        super("Utilisateur introuvable avec l'identifiant : " + id);
    }
}

