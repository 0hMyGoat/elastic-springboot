package fr.octocorn.elasticspringboot.job.application;

import fr.octocorn.elasticspringboot.job.application.view.JobDetailView;
import fr.octocorn.elasticspringboot.job.application.view.JobView;
import fr.octocorn.elasticspringboot.job.domain.job.JobRepository;
import fr.octocorn.elasticspringboot.job.domain.exception.JobNotFoundException;
import fr.octocorn.elasticspringboot.job.infrastructure.mapper.JobMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    /**
     * Retourne la liste paginée des métiers.
     */
    public Page<JobView> findAll(Pageable pageable) {
        return jobRepository.findAll(pageable).map(jobMapper::toView);
    }

    /**
     * Retourne le détail d'un métier par son identifiant.
     *
     * @param id identifiant du métier
     * @return la vue détaillée du métier
     * @throws JobNotFoundException si le métier est introuvable
     */
    public JobDetailView findById(UUID id) {
        return jobRepository.findById(id)
                .map(jobMapper::toDetailView)
                .orElseThrow(() -> new JobNotFoundException(id));
    }
}

