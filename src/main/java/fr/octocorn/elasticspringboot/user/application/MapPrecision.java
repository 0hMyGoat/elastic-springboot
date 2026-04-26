package fr.octocorn.elasticspringboot.user.application;

/**
 * Niveau de précision d'affichage sur la carte.
 * Le frontend choisit la précision selon le niveau de zoom Leaflet.
 */
public enum MapPrecision {
    /** Agrégation par ville. Zoom ≤ 8. */
    CITY,
    /** Agrégation par code postal. Zoom 9–12. */
    POSTAL_CODE,
    /** Un point par individu. Zoom ≥ 13. */
    INDIVIDUAL
}

