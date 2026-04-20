package fr.octocorn.elasticspringboot.user.dto;

import java.util.UUID;

public record EmailDTO(
        UUID id,
        String email
) {}

