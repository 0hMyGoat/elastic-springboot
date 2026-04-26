package fr.octocorn.elasticspringboot.job.api;

import fr.octocorn.elasticspringboot.job.application.JobService;
import fr.octocorn.elasticspringboot.job.application.view.JobDetailView;
import fr.octocorn.elasticspringboot.job.application.view.JobView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Jobs", description = "Référentiel des métiers")
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "Lister les métiers", description = "Retourne la liste paginée des métiers avec leur secteur.")
    @GetMapping
    public Page<JobView> findAll(@ParameterObject Pageable pageable) {
        return jobService.findAll(pageable);
    }

    @Operation(summary = "Récupérer un métier par son identifiant", description = "Retourne le détail d'un métier incluant son secteur.")
    @GetMapping("/{id}")
    public JobDetailView findById(@PathVariable UUID id) {
        return jobService.findById(id);
    }
}

