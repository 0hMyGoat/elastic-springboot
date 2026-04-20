package fr.octocorn.elasticspringboot.job.sector.mapper;

import fr.octocorn.elasticspringboot.job.sector.Sector;
import fr.octocorn.elasticspringboot.job.sector.dto.SectorDTO;
import fr.octocorn.elasticspringboot.job.sector.dto.SectorDetailDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {fr.octocorn.elasticspringboot.job.job.mapper.JobMapper.class})
public interface SectorMapper {

    SectorDTO toDTO(Sector sector);

    List<SectorDTO> toDTOList(List<Sector> sectors);

    SectorDetailDTO toDetailDTO(Sector sector);
}

