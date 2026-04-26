package fr.octocorn.elasticspringboot.user.application;

import fr.octocorn.elasticspringboot.user.application.query.UserSearchCriteriaRequest;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserDocument;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserSearchQueryBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final UserSearchQueryBuilder queryBuilder;

    /**
     * Recherche plein-texte des utilisateurs sur le prénom et le nom.
     *
     * @param query    texte libre de recherche
     * @param pageable paramètres de pagination
     * @return page des résultats de recherche
     */
    public Page<UserSearchResult> rechercherParTexte(String query, Pageable pageable) {
        NativeQuery nativeQuery = queryBuilder.buildFullText(query, pageable);
        return executerRecherche(nativeQuery, pageable);
    }

    /**
     * Recherche structurée des utilisateurs à partir des critères POST.
     *
     * @param criteria critères structurés de recherche
     * @return page des résultats de recherche
     */
    public Page<UserSearchResult> rechercherParCriteres(@Valid UserSearchCriteriaRequest criteria) {
        NativeQuery nativeQuery = queryBuilder.buildFromRequest(criteria);
        return executerRecherche(nativeQuery, criteria.toPageable());
    }

    private Page<UserSearchResult> executerRecherche(NativeQuery nativeQuery, Pageable pageable) {
        List<UserSearchResult> results = elasticsearchOperations
                .search(nativeQuery, UserDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .map(this::toResult)
                .toList();
        long total = elasticsearchOperations.count(nativeQuery, UserDocument.class);
        return new PageImpl<>(results, pageable, total);
    }

    private UserSearchResult toResult(UserDocument document) {
        return new UserSearchResult(
                document.getId(),
                document.getFirstName(),
                document.getLastName(),
                document.getCity(),
                document.getPostalCode(),
                document.getLocation(),
                document.getJobName(),
                document.getSectorName(),
                document.getEmails(),
                document.getPhones()
        );
    }
}
