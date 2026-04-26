package fr.octocorn.elasticspringboot.user.application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record UserSearchCriteria(
        String query,
        String firstName,
        String lastName,
        String city,
        String postalCode,
        String jobName,
        String sectorName,
        @Min(0) Integer page,
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