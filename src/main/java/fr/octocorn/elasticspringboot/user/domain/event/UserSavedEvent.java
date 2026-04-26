package fr.octocorn.elasticspringboot.user.domain.event;

import java.util.UUID;

/** * Événement émis après la persistance d'un utilisateur (création ou mise à jour). * * @param userId identifiant de l'utilisateur à indexer */
public record UserSavedEvent(UUID userId) {}