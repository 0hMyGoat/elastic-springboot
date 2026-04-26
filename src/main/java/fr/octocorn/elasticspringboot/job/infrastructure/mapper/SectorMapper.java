package fr.octocorn.elasticspringboot.job.infrastructure.mapper;

import fr.octocorn.elasticspringboot.job.application.view.SectorDetailView;
import fr.octocorn.elasticspringboot.job.application.view.SectorView;
import fr.octocorn.elasticspringboot.job.domain.sector.Sector;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JobMapper.class})
public interface SectorMapper {

    SectorView toView(Sector sector);

    List<SectorView> toViewList(List<Sector> sectors);

    SectorDetailView toDetailView(Sector sector);
}

