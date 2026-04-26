package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import fr.octocorn.elasticspringboot.user.application.query.UserSearchCriteriaRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserSearchQueryBuilder {

    /**
     * Construit une requête Elasticsearch full-text sur le prénom et le nom.
     *
     * @param query    texte libre de recherche
     * @param pageable paramètres de pagination
     * @return la requête native Elasticsearch paginée
     */
    public NativeQuery buildFullText(String query, Pageable pageable) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
        ajouterRechercheLibre(bool, query);
        return NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withPageable(pageable)
                .build();
    }

    /**
     * Construit une requête Elasticsearch native à partir des critères structurés (POST body).
     *
     * @param criteria critères structurés de recherche
     * @return la requête native Elasticsearch paginée
     */
    public NativeQuery buildFromRequest(UserSearchCriteriaRequest criteria) {
        BoolQuery.Builder bool = new BoolQuery.Builder();

        ajouterFiltreFuzzy(bool, "firstName", criteria.firstName());
        ajouterFiltreFuzzy(bool, "lastName", criteria.lastName());
        ajouterFiltreFuzzy(bool, "city", criteria.city());
        ajouterFiltreExact(bool, "postalCode", criteria.postalCode());
        ajouterFiltreExactUuid(bool, "jobId", criteria.jobId());
        ajouterFiltreExactUuid(bool, "sectorId", criteria.sectorId());

        return NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(bool.build())))
                .withPageable(criteria.toPageable())
                .build();
    }

    private void ajouterRechercheLibre(BoolQuery.Builder bool, String query) {
        if (estVide(query)) return;
        bool.must(q -> q.multiMatch(mm -> mm
                .query(query)
                .fields("firstName^3", "lastName^3", "jobName^2", "sectorName^2", "city")
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

    private void ajouterFiltreExactUuid(BoolQuery.Builder bool, String champ, UUID valeur) {
        if (valeur == null) return;
        bool.filter(q -> q.term(t -> t
                .field(champ)
                .value(valeur.toString())
        ));
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
