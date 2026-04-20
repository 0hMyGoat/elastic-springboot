package fr.octocorn.elasticspringboot.user.mapper;

import fr.octocorn.elasticspringboot.user.dto.*;
import fr.octocorn.elasticspringboot.user.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTOList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "contactInfos", ignore = true)
    @Mapping(target = "userJobs", ignore = true)
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "contactInfos", ignore = true)
    @Mapping(target = "userJobs", ignore = true)
    void updateEntity(UserUpdateDTO dto, @MappingTarget User user);

    ContactInfoDTO toDTO(ContactInfo contactInfo);

    AddressDTO toDTO(Address address);

    PhoneDTO toDTO(Phone phone);

    EmailDTO toDTO(Email email);

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobName", source = "job.name")
    @Mapping(target = "sectorName", source = "job.sector.name")
    UserJobDTO toDTO(UserJob userJob);
}

