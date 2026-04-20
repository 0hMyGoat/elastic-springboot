package fr.octocorn.elasticspringboot.user.dto;

import fr.octocorn.elasticspringboot.user.model.ContactType;

import java.util.List;
import java.util.UUID;

public record ContactInfoDTO(
        UUID id,
        ContactType type,
        String label,
        List<AddressDTO> addresses,
        List<PhoneDTO> phones,
        List<EmailDTO> emails
) {}

