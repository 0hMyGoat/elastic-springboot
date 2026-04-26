package fr.octocorn.elasticspringboot.user.application.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserView(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        LocalDateTime registeredAt,
        List<ContactInfoView> contactInfos,
        List<UserJobView> userJobs
) {}

