package fr.octocorn.elasticspringboot.user.application;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchCriteria;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchResult;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserDocument;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserSearchQueryBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
     * Recherche des utilisateurs à partir des critères fournis.
     * Les r�sultats proviennent directement de l'index Elasticsearch.
     * Pour le détail complet d'un utilisateur, utiliser GET /users/{id}.
     *
     * @param criteria critères de recherche
     * @return page des résultats de recherche
     */
    public Page<UserSearchResult> search(@Valid UserSearchCriteria criteria) {
        NativeQuery nativeQuery = queryBuilder.build(criteria);
        List<UserSearchResult> results = elasticsearchOperations
                .search(nativeQuery, UserDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .map(this::toResult)
                .toList();
        long total = elasticsearchOperations.count(nativeQuery, UserDocument.class);
        return new PageImpl<>(results, criteria.toPageable(), total);
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
