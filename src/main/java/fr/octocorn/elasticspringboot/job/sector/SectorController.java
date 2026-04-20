package fr.octocorn.elasticspringboot.job.sector;

import fr.octocorn.elasticspringboot.job.sector.dto.SectorDetailDTO;
import fr.octocorn.elasticspringboot.job.sector.dto.SectorDTO;
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
    public Page<SectorDTO> findAll(@ParameterObject Pageable pageable) {
        return sectorService.findAll(pageable);
    }

    @Operation(summary = "Récupérer un secteur par son identifiant", description = "Retourne le détail d'un secteur incluant la liste de ses métiers.")
    @GetMapping("/{id}")
    public SectorDetailDTO findById(@PathVariable UUID id) {
        return sectorService.findById(id);
    }
}

