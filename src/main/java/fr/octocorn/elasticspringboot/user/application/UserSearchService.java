package fr.octocorn.elasticspringboot.user.application;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import fr.octocorn.elasticspringboot.user.application.view.UserView;
import fr.octocorn.elasticspringboot.user.domain.UserRepository;
import fr.octocorn.elasticspringboot.user.domain.model.User;
import fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch.UserDocument;
import fr.octocorn.elasticspringboot.user.infrastructure.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Recherche des utilisateurs à partir des critères fournis.
     * La recherche est effectuée dans Elasticsearch, puis les données complètes
     * sont rechargées depuis la base de données.
     *
     * @param criteria critères de recherche
     * @return page des utilisateurs correspondants
     */
    public Page<UserView> search(@Valid UserSearchCriteria criteria) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(buildQuery(criteria))
                .withPageable(criteria.toPageable())
                .build();

        List<UserDocument> documents = elasticsearchOperations
                .search(nativeQuery, UserDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();

        long total = elasticsearchOperations.count(nativeQuery, UserDocument.class);
        List<UserView> views = chargerUtilisateurs(documents);

        return new PageImpl<>(views, criteria.toPageable(), total);
    }

    private Query buildQuery(UserSearchCriteria criteria) {
        BoolQuery.Builder bool = new BoolQuery.Builder();

        ajouterRechercheLibre(bool, criteria.query());
        ajouterFiltreFuzzy(bool, "firstName", criteria.firstName());
        ajouterFiltreFuzzy(bool, "lastName", criteria.lastName());
        ajouterFiltreExact(bool, "city", criteria.city());
        ajouterFiltreExact(bool, "postalCode", criteria.postalCode());
        ajouterFiltreExact(bool, "jobName", criteria.jobName());
        ajouterFiltreExact(bool, "sectorName", criteria.sectorName());

        return Query.of(q -> q.bool(bool.build()));
    }

    private void ajouterRechercheLibre(BoolQuery.Builder bool, String query) {
        if (estVide(query)) {
            return;
        }
        bool.must(q -> q.multiMatch(mm -> mm
                .query(query)
                .fields("firstName", "lastName")
                .fuzziness("AUTO")
        ));
    }

    private void ajouterFiltreFuzzy(BoolQuery.Builder bool, String champ, String valeur) {
        if (estVide(valeur)) {
            return;
        }
        bool.must(q -> q.fuzzy(f -> f
                .field(champ)
                .value(valeur)
                .fuzziness("AUTO")
        ));
    }

    private void ajouterFiltreExact(BoolQuery.Builder bool, String champ, String valeur) {
        if (estVide(valeur)) {
            return;
        }
        bool.filter(q -> q.term(t -> t
                .field(champ)
                .value(valeur)
        ));
    }

    private List<UserView> chargerUtilisateurs(List<UserDocument> documents) {
        List<UUID> ids = documents.stream()
                .map(UserDocument::getId)
                .toList();

        Map<UUID, User> usersParId = userRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return ids.stream()
                .map(usersParId::get)
                .filter(Objects::nonNull)
                .map(userMapper::toView)
                .toList();
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
