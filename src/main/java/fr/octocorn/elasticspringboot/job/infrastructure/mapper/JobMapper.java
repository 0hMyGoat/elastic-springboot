package fr.octocorn.elasticspringboot.job.infrastructure.mapper;

import fr.octocorn.elasticspringboot.job.application.view.JobDetailView;
import fr.octocorn.elasticspringboot.job.application.view.JobView;
import fr.octocorn.elasticspringboot.job.domain.job.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "sectorId", source = "sector.id")
    @Mapping(target = "sectorName", source = "sector.name")
    JobView toView(Job job);

    List<JobView> toViewList(List<Job> jobs);

    JobDetailView toDetailView(Job job);
}

