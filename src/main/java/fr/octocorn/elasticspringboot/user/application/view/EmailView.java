package fr.octocorn.elasticspringboot.user.application.view;

import java.util.UUID;

public record EmailView(
        UUID id,
        String email
) {}

