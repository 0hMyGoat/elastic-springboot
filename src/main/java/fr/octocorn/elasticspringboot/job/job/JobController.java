package fr.octocorn.elasticspringboot.job.job;

import fr.octocorn.elasticspringboot.job.job.dto.JobDTO;
import fr.octocorn.elasticspringboot.job.job.dto.JobDetailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Jobs", description = "Référentiel des métiers")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "Lister les métiers", description = "Retourne la liste paginée des métiers avec leur secteur.")
    @GetMapping
    public Page<JobDTO> findAll(Pageable pageable) {
        return jobService.findAll(pageable);
    }

    @Operation(summary = "Récupérer un métier par son identifiant", description = "Retourne le détail d'un métier incluant son secteur.")
    @GetMapping("/{id}")
    public JobDetailDTO findById(@PathVariable UUID id) {
        return jobService.findById(id);
    }
}

