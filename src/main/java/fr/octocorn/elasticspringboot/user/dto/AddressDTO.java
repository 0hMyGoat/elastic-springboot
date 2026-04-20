package fr.octocorn.elasticspringboot.user.dto;

import java.util.UUID;

public record AddressDTO(
        UUID id,
        String streetLine1,
        String streetLine2,
        String postalCode,
        String city,
        String state,
        String country
) {}

