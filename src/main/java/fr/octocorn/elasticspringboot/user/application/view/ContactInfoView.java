package fr.octocorn.elasticspringboot.user.application.view;

import fr.octocorn.elasticspringboot.user.domain.model.contact.ContactType;

import java.util.List;
import java.util.UUID;

public record ContactInfoView(
        UUID id,
        ContactType type,
        String label,
        List<AddressView> addresses,
        List<PhoneView> phones,
        List<EmailView> emails
) {}

