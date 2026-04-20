package fr.octocorn.elasticspringboot.user.dto;

import fr.octocorn.elasticspringboot.user.model.PhoneType;

import java.util.UUID;

public record PhoneDTO(
        UUID id,
        PhoneType type,
        String number
) {}

