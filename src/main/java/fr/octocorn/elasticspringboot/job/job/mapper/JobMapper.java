package fr.octocorn.elasticspringboot.job.job.mapper;

import fr.octocorn.elasticspringboot.job.job.Job;
import fr.octocorn.elasticspringboot.job.job.dto.JobDTO;
import fr.octocorn.elasticspringboot.job.job.dto.JobDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "sectorId", source = "sector.id")
    @Mapping(target = "sectorName", source = "sector.name")
    JobDTO toDTO(Job job);

    List<JobDTO> toDTOList(List<Job> jobs);

    JobDetailDTO toDetailDTO(Job job);
}

