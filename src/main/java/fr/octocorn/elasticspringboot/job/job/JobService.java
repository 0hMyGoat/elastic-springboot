package fr.octocorn.elasticspringboot.job.job;

import fr.octocorn.elasticspringboot.job.job.dto.JobDTO;
import fr.octocorn.elasticspringboot.job.job.dto.JobDetailDTO;
import fr.octocorn.elasticspringboot.job.job.exception.JobNotFoundException;
import fr.octocorn.elasticspringboot.job.job.mapper.JobMapper;
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

    public Page<JobDTO> findAll(Pageable pageable) {
        return jobRepository.findAll(pageable).map(jobMapper::toDTO);
    }

    public JobDetailDTO findById(UUID id) {
        return jobRepository.findById(id)
                .map(jobMapper::toDetailDTO)
                .orElseThrow(() -> new JobNotFoundException(id));
    }
}

