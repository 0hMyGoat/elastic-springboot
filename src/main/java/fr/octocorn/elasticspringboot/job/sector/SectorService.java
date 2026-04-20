package fr.octocorn.elasticspringboot.job.sector;

import fr.octocorn.elasticspringboot.job.sector.dto.SectorDetailDTO;
import fr.octocorn.elasticspringboot.job.sector.dto.SectorDTO;
import fr.octocorn.elasticspringboot.job.sector.exception.SectorNotFoundException;
import fr.octocorn.elasticspringboot.job.sector.mapper.SectorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectorService {

    private final SectorRepository sectorRepository;
    private final SectorMapper sectorMapper;

    public Page<SectorDTO> findAll(Pageable pageable) {
        return sectorRepository.findAll(pageable).map(sectorMapper::toDTO);
    }

    public SectorDetailDTO findById(UUID id) {
        return sectorRepository.findById(id)
                .map(sectorMapper::toDetailDTO)
                .orElseThrow(() -> new SectorNotFoundException(id));
    }
}

