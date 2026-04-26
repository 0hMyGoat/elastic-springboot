package fr.octocorn.elasticspringboot.user.application.view;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Un point sur la carte — soit un cluster agrégé (ville/code postal) soit un individu.
 *
 * @param label  Libellé affiché (nom de ville, code postal ou "Prénom Nom")
 * @param count  Nombre de personnes représentées (1 pour un individu)
 * @param lat    Latitude du point (centroïde pour les clusters)
 * @param lon    Longitude du point
 * @param userId UUID de l'utilisateur (non-null uniquement si precision = INDIVIDUAL)
 */
@Schema(description = "Point géographique affiché sur la carte")
public record MapPointView(
        String label,
        long count,
        double lat,
        double lon,
        String userId
) {}

