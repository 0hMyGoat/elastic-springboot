package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchCriteria;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;

@Component
public class UserSearchQueryBuilder {

    /**
     * Construit la requête Elasticsearch native à partir des critères fournis.
     *
     * @param criteria critères de recherche
     * @return la requête native Elasticsearch paginée
     */
    public NativeQuery build(UserSearchCriteria criteria) {
        return NativeQuery.builder()
                .withQuery(buildQuery(criteria))
                .withPageable(criteria.toPageable())
                .build();
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
        if (estVide(query)) return;
        bool.must(q -> q.multiMatch(mm -> mm
                .query(query)
                .fields("firstName", "lastName")
                .fuzziness("AUTO")
        ));
    }

    private void ajouterFiltreFuzzy(BoolQuery.Builder bool, String champ, String valeur) {
        if (estVide(valeur)) return;
        bool.must(q -> q.fuzzy(f -> f
                .field(champ)
                .value(valeur)
                .fuzziness("AUTO")
        ));
    }

    private void ajouterFiltreExact(BoolQuery.Builder bool, String champ, String valeur) {
        if (estVide(valeur)) return;
        bool.filter(q -> q.term(t -> t
                .field(champ)
                .value(valeur)
        ));
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}

