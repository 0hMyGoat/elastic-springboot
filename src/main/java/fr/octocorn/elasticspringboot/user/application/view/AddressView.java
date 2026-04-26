package fr.octocorn.elasticspringboot.user.application.view;

import java.util.UUID;

public record AddressView(
        UUID id,
        String streetLine1,
        String streetLine2,
        String postalCode,
        String city,
        String state,
        String country
) {}

