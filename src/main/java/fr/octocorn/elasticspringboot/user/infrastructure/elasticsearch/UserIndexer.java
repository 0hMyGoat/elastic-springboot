package fr.octocorn.elasticspringboot.user.infrastructure.elasticsearch;


import fr.octocorn.elasticspringboot.infrastructure.geo.CoordinatesLookup;
import fr.octocorn.elasticspringboot.user.domain.UserRepository;
import fr.octocorn.elasticspringboot.user.domain.model.User;
import fr.octocorn.elasticspringboot.user.domain.model.UserJob;
import fr.octocorn.elasticspringboot.user.domain.model.contact.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserIndexer {

    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;
    private final CoordinatesLookup coordinatesLookup;

    /**
     * Synchronise l'ensemble des utilisateurs existants vers Elasticsearch par batch de 500.
     */
    public void synchronizeAll() {
        int page = 0;
        final int batchSize = 500;
        Page<User> batch;

        do {
            batch = userRepository.findAll(PageRequest.of(page, batchSize));
            List<UserDocument> documents = batch.getContent().stream()
                    .map(this::toDocument)
                    .toList();
            userSearchRepository.saveAll(documents);
            log.info("[UserIndexer] Batch {} indexé ({} utilisateurs).", page, documents.size());
            page++;
        } while (batch.hasNext());
    }

    /**
     * Indexe ou met à jour un utilisateur dans Elasticsearch.     *     * @param userId identifiant de l'utilisateur à indexer
     */
    public void index(UUID userId) {
        userRepository.findById(userId).ifPresentOrElse(
                user -> {
                    userSearchRepository.save(toDocument(user));
                    log.debug("[UserIndexer] Utilisateur {} indexé.", userId);
                },
                () -> log.warn("[UserIndexer] Utilisateur {} introuvable, indexation ignorée.", userId)
        );
    }

    /**
     * Supprime un utilisateur de l'index Elasticsearch.     *     * @param userId identifiant de l'utilisateur à supprimer
     */
    public void delete(UUID userId) {
        userSearchRepository.deleteById(userId);
        log.debug("[UserIndexer] Utilisateur {} supprimé de l'index.", userId);
    }

    private GeoPoint resolveGeoPoint(Address address) {
        if (address == null || address.getPostalCode() == null) return null;
        return coordinatesLookup.resolve(address.getPostalCode())
                .map(c -> new GeoPoint(c.latitude(), c.longitude()))
                .orElse(null);
    }

    private UserDocument toDocument(User user) {
        Optional<ContactInfo> workPrimary = user.getContactInfos().stream()
                .filter(c -> c.getType() == ContactType.WORK_PRIMARY)
                .findFirst();

        Address address = workPrimary
                .flatMap(c -> c.getAddresses().stream().findFirst())
                .orElseGet(() -> user.getContactInfos().stream()
                        .flatMap(c -> c.getAddresses().stream())
                        .findFirst()
                        .orElse(null));

        List<String> emails = user.getContactInfos().stream()
                .flatMap(c -> c.getEmails().stream())
                .map(Email::getEmail)
                .toList();

        List<String> phones = user.getContactInfos().stream()
                .flatMap(c -> c.getPhones().stream())
                .map(Phone::getNumber)
                .toList();

        Optional<UserJob> primaryJob = user.getUserJobs().stream()
                .filter(UserJob::getPrimaryJob)
                .findFirst();

        return UserDocument.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .city(address != null ? address.getCity() : null)
                .postalCode(address != null ? address.getPostalCode() : null)
                .location(resolveGeoPoint(address))
                .jobName(primaryJob.map(uj -> uj.getJob().getName()).orElse(null))
                .sectorName(primaryJob.map(uj -> uj.getJob().getSector().getName()).orElse(null))
                .emails(emails)
                .phones(phones)
                .build();
    }
}