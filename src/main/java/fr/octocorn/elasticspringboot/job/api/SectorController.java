package fr.octocorn.elasticspringboot.job.api;

import fr.octocorn.elasticspringboot.job.application.SectorService;
import fr.octocorn.elasticspringboot.job.application.view.SectorDetailView;
import fr.octocorn.elasticspringboot.job.application.view.SectorView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Sectors", description = "Référentiel des secteurs d'activité")
@RestController
@RequestMapping("/sectors")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @Operation(summary = "Lister les secteurs", description = "Retourne la liste paginée des secteurs d'activité.")
    @GetMapping
    public Page<SectorView> findAll(@ParameterObject Pageable pageable) {
        return sectorService.findAll(pageable);
    }

    @Operation(summary = "Récupérer un secteur par son identifiant", description = "Retourne le détail d'un secteur incluant la liste de ses métiers.")
    @GetMapping("/{id}")
    public SectorDetailView findById(@PathVariable UUID id) {
        return sectorService.findById(id);
    }
}

