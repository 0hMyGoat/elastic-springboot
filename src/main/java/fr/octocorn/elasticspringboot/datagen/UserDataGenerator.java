package fr.octocorn.elasticspringboot.datagen;

import fr.octocorn.elasticspringboot.job.domain.job.Job;
import fr.octocorn.elasticspringboot.job.domain.job.JobRepository;
import fr.octocorn.elasticspringboot.user.domain.*;
import fr.octocorn.elasticspringboot.user.domain.model.User;
import fr.octocorn.elasticspringboot.user.domain.model.UserJob;
import fr.octocorn.elasticspringboot.user.domain.model.contact.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("datagen")
@RequiredArgsConstructor
public class UserDataGenerator {

    private static final int TOTAL_USERS = 10_000;
    private static final int BATCH_SIZE  = 500;

    private final UserRepository userRepository;
    private final JobRepository  jobRepository;

    private final Faker  fakerFrCa  = new Faker(Locale.CANADA_FRENCH);
    private final Random random     = new Random();
    private final Set<String> usedEmails = new HashSet<>();

    /**
     * Génère 10 000 utilisateurs avec leurs coordonnées et métiers.
     */
    public void generate() {
        if (userRepository.count() > 0) {
            log.info("[UserDataGenerator] Users already exist, skipping.");
            return;
        }

        List<AddressRow> addresses = loadAddresses();
        if (addresses.isEmpty()) {
            log.error("[UserDataGenerator] No addresses loaded from CSV.");
            return;
        }

        Map<String, List<Job>> jobsBySector = jobRepository.findAllWithSector()
                .stream()
                .collect(Collectors.groupingBy(j -> j.getSector().getCode()));

        if (jobsBySector.isEmpty()) {
            log.error("[UserDataGenerator] No jobs found — run JobDataGenerator first.");
            return;
        }

        List<String> sectorCodes = new ArrayList<>(jobsBySector.keySet());
        List<User>   batch       = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < TOTAL_USERS; i++) {
            batch.add(generateUser(addresses, jobsBySector, sectorCodes));

            if (batch.size() == BATCH_SIZE) {
                userRepository.saveAll(batch);
                log.info("[UserDataGenerator] {}/{} users saved.", i + 1, TOTAL_USERS);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            userRepository.saveAll(batch);
        }

        log.info("[UserDataGenerator] {} users generated.", TOTAL_USERS);
    }

    // -------------------------------------------------------------------------
    // Chargement du CSV
    // -------------------------------------------------------------------------

    /**
     * Charge les adresses depuis src/main/resources/data/addresses_qc.csv.
     */
    private List<AddressRow> loadAddresses() {
        List<AddressRow> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/addresses_qc.csv").getInputStream(),
                StandardCharsets.UTF_8))) {

            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (cols.length < 6) continue;

                rows.add(new AddressRow(
                        unquote(cols[0]),
                        unquote(cols[1]),
                        unquote(cols[2]),
                        unquote(cols[3]),
                        unquote(cols[4]),
                        unquote(cols[5])
                ));
            }
        } catch (Exception e) {
            log.error("[UserDataGenerator] Failed to load addresses CSV: {}", e.getMessage());
        }

        log.info("[UserDataGenerator] {} addresses loaded from CSV.", rows.size());
        return rows;
    }

    private String unquote(String value) {
        return value.trim().replaceAll("^\"|\"$", "");
    }

    private record AddressRow(
            String streetLine1,
            String postalCode,
            String city,
            String state,
            String longitude,
            String latitude
    ) {}

    // -------------------------------------------------------------------------
    // Génération d'un utilisateur complet
    // -------------------------------------------------------------------------

    private User generateUser(List<AddressRow> addresses,
                               Map<String, List<Job>> jobsBySector,
                               List<String> sectorCodes) {
        User user = User.builder()
                .firstName(fakerFrCa.name().firstName())
                .lastName(fakerFrCa.name().lastName())
                .birthDate(randomBirthDate())
                .registeredAt(randomRegistrationDate())
                .build();

        user.setContactInfos(generateContactInfos(user, addresses));
        user.setUserJobs(generateUserJobs(user, jobsBySector, sectorCodes));

        return user;
    }

    // -------------------------------------------------------------------------
    // Coordonnées
    // -------------------------------------------------------------------------

    /**
     * Distribution : 10% perso seule | 30% perso+1pro | 30% perso+2pro | 30% 2pro seules
     */
    private List<ContactInfo> generateContactInfos(User user, List<AddressRow> addresses) {
        int roll = random.nextInt(100);

        if (roll < 10) {
            return List.of(
                    buildContactInfo(user, addresses, ContactType.PERSONAL, "Personnel")
            );
        } else if (roll < 40) {
            return List.of(
                    buildContactInfo(user, addresses, ContactType.PERSONAL,     "Personnel"),
                    buildContactInfo(user, addresses, ContactType.WORK_PRIMARY, "Pro principal")
            );
        } else if (roll < 70) {
            return List.of(
                    buildContactInfo(user, addresses, ContactType.PERSONAL,       "Personnel"),
                    buildContactInfo(user, addresses, ContactType.WORK_PRIMARY,   "Pro principal"),
                    buildContactInfo(user, addresses, ContactType.WORK_SECONDARY, "Pro secondaire")
            );
        } else {
            return List.of(
                    buildContactInfo(user, addresses, ContactType.WORK_PRIMARY,   "Pro principal"),
                    buildContactInfo(user, addresses, ContactType.WORK_SECONDARY, "Pro secondaire")
            );
        }
    }

    private ContactInfo buildContactInfo(User user, List<AddressRow> addresses,
                                          ContactType type, String label) {
        ContactInfo contactInfo = ContactInfo.builder()
                .type(type)
                .label(label)
                .user(user)
                .build();

        AddressRow row = addresses.get(random.nextInt(addresses.size()));

        Address address = Address.builder()
                .streetLine1(row.streetLine1())
                .postalCode(row.postalCode())
                .city(row.city())
                .state(row.state())
                .country("Canada")
                .contactInfo(contactInfo)
                .build();

        List<Phone> phones = new ArrayList<>();
        phones.add(Phone.builder()
                .type(PhoneType.MOBILE)
                .number(fakerFrCa.phoneNumber().cellPhone())
                .contactInfo(contactInfo)
                .build());

        if (random.nextInt(100) < 30) {
            phones.add(Phone.builder()
                    .type(PhoneType.LANDLINE)
                    .number(fakerFrCa.phoneNumber().phoneNumber())
                    .contactInfo(contactInfo)
                    .build());
        }

        Email email = Email.builder()
                .email(generateUniqueEmail(user.getFirstName(), user.getLastName()))
                .contactInfo(contactInfo)
                .build();

        contactInfo.setAddresses(List.of(address));
        contactInfo.setPhones(phones);
        contactInfo.setEmails(List.of(email));

        return contactInfo;
    }

    // -------------------------------------------------------------------------
    // Métiers — 90% même secteur, 10% aléatoire
    // -------------------------------------------------------------------------

    private List<UserJob> generateUserJobs(User user,
                                           Map<String, List<Job>> jobsBySector,
                                           List<String> sectorCodes) {
        int     jobCount   = pickJobCount();
        boolean sameSector = random.nextInt(100) >= 10;

        String    primarySector = sectorCodes.get(random.nextInt(sectorCodes.size()));
        List<Job> pool = sameSector
                ? new ArrayList<>(jobsBySector.get(primarySector))
                : jobsBySector.values().stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

        Collections.shuffle(pool, random);
        List<Job> selected = pool.stream().limit(Math.min(jobCount, pool.size())).toList();

        List<UserJob> userJobs = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            LocalDate start = randomStartDate();
            userJobs.add(UserJob.builder()
                    .primaryJob(i == 0)
                    .startDate(start)
                    .endDate(i > 0 && random.nextBoolean()
                            ? start.plusYears(random.nextInt(1, 5))
                            : null)
                    .user(user)
                    .job(selected.get(i))
                    .build());
        }

        return userJobs;
    }

    /** 70% → 1 job, 25% → 2 jobs, 5% → 3 jobs */
    private int pickJobCount() {
        int roll = random.nextInt(100);
        if (roll < 70) return 1;
        if (roll < 95) return 2;
        return 3;
    }

    // -------------------------------------------------------------------------
    // Email unique
    // -------------------------------------------------------------------------

    private String generateUniqueEmail(String firstName, String lastName) {
        String provider = random.nextBoolean() ? "gmail.com" : "hotmail.com";
        String base     = normalize(firstName) + "." + normalize(lastName);
        String email    = base + "@" + provider;
        int    counter  = 1;

        while (usedEmails.contains(email)) {
            email = base + counter++ + "@" + provider;
        }

        usedEmails.add(email);
        return email;
    }

    /** Jean-François → jeanfrancois */
    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase();
    }

    // -------------------------------------------------------------------------
    // Dates
    // -------------------------------------------------------------------------

    private LocalDate randomBirthDate() {
        return LocalDate.now().minusYears(18 + random.nextInt(48));
    }

    private LocalDateTime randomRegistrationDate() {
        long origin = LocalDate.of(2015, 1, 1).toEpochDay();
        long bound  = LocalDate.now().toEpochDay();
        return LocalDate.ofEpochDay(origin + random.nextLong(bound - origin)).atStartOfDay();
    }

    private LocalDate randomStartDate() {
        return LocalDate.now().minusDays(random.nextLong(0, 365L * 10));
    }
}

