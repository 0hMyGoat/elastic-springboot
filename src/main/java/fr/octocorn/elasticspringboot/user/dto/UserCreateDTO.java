package fr.octocorn.elasticspringboot.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserCreateDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @Past
    private LocalDate birthDate;

    private List<ContactInfoDTO> contactInfos = new ArrayList<>();

    private List<UserJobDTO> userJobs = new ArrayList<>();
}

