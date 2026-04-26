package fr.octocorn.elasticspringboot.job.application;

import fr.octocorn.elasticspringboot.job.application.view.SectorDetailView;
import fr.octocorn.elasticspringboot.job.application.view.SectorView;
import fr.octocorn.elasticspringboot.job.domain.sector.SectorRepository;
import fr.octocorn.elasticspringboot.job.domain.exception.SectorNotFoundException;
import fr.octocorn.elasticspringboot.job.infrastructure.mapper.SectorMapper;
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

    /**
     * Retourne la liste paginée des secteurs.
     */
    public Page<SectorView> findAll(Pageable pageable) {
        return sectorRepository.findAll(pageable).map(sectorMapper::toView);
    }

    /**
     * Retourne le détail d'un secteur par son identifiant.
     *
     * @param id identifiant du secteur
     * @return la vue détaillée du secteur incluant ses métiers
     * @throws SectorNotFoundException si le secteur est introuvable
     */
    public SectorDetailView findById(UUID id) {
        return sectorRepository.findById(id)
                .map(sectorMapper::toDetailView)
                .orElseThrow(() -> new SectorNotFoundException(id));
    }
}

