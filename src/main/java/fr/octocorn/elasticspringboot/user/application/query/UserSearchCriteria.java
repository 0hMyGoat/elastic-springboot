package fr.octocorn.elasticspringboot.user.application.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Schema(description = "Critères de recherche d'utilisateurs")
public record UserSearchCriteria(

        @Schema(description = "Recherche libre sur le prénom et le nom (tolérance aux fautes de frappe)",
                example = "evan picard")
        String query,

        @Schema(description = "Filtre flou sur le prénom", example = "Evan")
        String firstName,

        @Schema(description = "Filtre flou sur le nom", example = "Picard")
        String lastName,

        @Schema(description = "Filtre exact sur la ville", example = "Sainte-Thérèse")
        String city,

        @Schema(description = "Filtre exact sur le code postal", example = "J7E3E7")
        String postalCode,

        @Schema(description = "Filtre exact sur le métier", example = "Chef Cuisinier")
        String jobName,

        @Schema(description = "Filtre exact sur le secteur d'activité", example = "Restauration & Hôtellerie")
        String sectorName,

        @Schema(description = "Numéro de page (0-based)", example = "0", defaultValue = "0")
        @Min(0) Integer page,

        @Schema(description = "Nombre de résultats par page (1-100)", example = "20", defaultValue = "20")
        @Min(1) @Max(100) Integer size

) {
    public UserSearchCriteria {
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}