package fr.octocorn.elasticspringboot.user.infrastructure.mapper;

import fr.octocorn.elasticspringboot.user.application.command.CreateUserCommand;
import fr.octocorn.elasticspringboot.user.application.command.UpdateUserCommand;
import fr.octocorn.elasticspringboot.user.application.view.*;
import fr.octocorn.elasticspringboot.user.domain.model.User;
import fr.octocorn.elasticspringboot.user.domain.model.UserJob;
import fr.octocorn.elasticspringboot.user.domain.model.contact.Address;
import fr.octocorn.elasticspringboot.user.domain.model.contact.ContactInfo;
import fr.octocorn.elasticspringboot.user.domain.model.contact.Email;
import fr.octocorn.elasticspringboot.user.domain.model.contact.Phone;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserView toView(User user);

    List<UserView> toViewList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "contactInfos", ignore = true)
    @Mapping(target = "userJobs", ignore = true)
    User toEntity(CreateUserCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "contactInfos", ignore = true)
    @Mapping(target = "userJobs", ignore = true)
    void updateEntity(UpdateUserCommand command, @MappingTarget User user);

    ContactInfoView toView(ContactInfo contactInfo);

    AddressView toView(Address address);

    PhoneView toView(Phone phone);

    EmailView toView(Email email);

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobName", source = "job.name")
    @Mapping(target = "sectorName", source = "job.sector.name")
    UserJobView toView(UserJob userJob);
}

