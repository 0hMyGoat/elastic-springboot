package fr.octocorn.elasticspringboot.user.application.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Critères structurés de recherche d'utilisateurs, transmis en corps de requête POST.
 */
@Schema(description = "Critères structurés de recherche d'utilisateurs")
public record UserSearchCriteriaRequest(

        @Schema(description = "Filtre flou sur le prénom", example = "Evan")
        @Size(max = 100)
        String firstName,

        @Schema(description = "Filtre flou sur le nom", example = "Picard")
        @Size(max = 100)
        String lastName,

        @Schema(description = "Filtre exact sur la ville", example = "Sainte-Thérèse")
        @Size(max = 100)
        String city,

        @Schema(description = "Filtre exact sur le code postal", example = "J7E3E7")
        @Size(max = 10)
        String postalCode,

        @Schema(description = "Filtre exact sur le métier", example = "Chef Cuisinier")
        @Size(max = 100)
        String jobName,

        @Schema(description = "Filtre exact sur le secteur d'activité", example = "Restauration & Hôtellerie")
        @Size(max = 100)
        String sectorName,

        @Schema(description = "Numéro de page (0-based)", example = "0", defaultValue = "0")
        @Min(0)
        Integer page,

        @Schema(description = "Nombre de résultats par page (1-100)", example = "20", defaultValue = "20")
        @Min(1) @Max(100)
        Integer size

) {
    public UserSearchCriteriaRequest {
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
    }

    /**
     * Construit un {@link Pageable} à partir des critères de pagination.
     *
     * @return pageable correspondant
     */
    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}

