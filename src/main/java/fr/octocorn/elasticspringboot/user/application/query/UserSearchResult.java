package fr.octocorn.elasticspringboot.user.application.query;


import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;
import java.util.UUID;

/** * Résultat d'une recherche d'utilisateur, basé sur les données indexées dans Elasticsearch. * Pour le détail complet, utiliser GET /users/{id}. */
public record UserSearchResult(
        UUID id,
        String firstName,
        String lastName,
        String city,
        String postalCode,
        GeoPoint location,
        String jobName,
        String sectorName,
        List<String> emails,
        List<String> phones
) {}