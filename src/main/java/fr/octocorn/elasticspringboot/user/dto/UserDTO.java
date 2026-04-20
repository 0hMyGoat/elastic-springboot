package fr.octocorn.elasticspringboot.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        LocalDateTime registeredAt,
        List<ContactInfoDTO> contactInfos,
        List<UserJobDTO> userJobs
) {}

