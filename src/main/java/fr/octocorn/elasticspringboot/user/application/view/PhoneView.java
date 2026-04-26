package fr.octocorn.elasticspringboot.user.application.view;

import fr.octocorn.elasticspringboot.user.domain.model.contact.PhoneType;

import java.util.UUID;

public record PhoneView(
        UUID id,
        PhoneType type,
        String number
) {}

