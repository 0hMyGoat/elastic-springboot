package fr.octocorn.elasticspringboot.infrastructure.geo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Charge les coordonnées GPS depuis le fichier CSV des adresses québécoises
 * et permet une résolution par code postal au moment de l'indexation.
 */
@Slf4j
@Component
public class CoordinatesLookup {

    private static final String CSV_PATH = "data/addresses_qc.csv";

    private final Map<String, Coordinates> coordinatesByPostalCode = new HashMap<>();

    public record Coordinates(double latitude, double longitude) {}

    @PostConstruct
    public void load() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8))) {

            reader.readLine(); // skip header

            String line;
            int loaded = 0;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (cols.length < 6) continue;

                String postalCode = unquote(cols[1]);
                String longitudeStr = unquote(cols[4]);
                String latitudeStr = unquote(cols[5]);

                try {
                    double latitude = Double.parseDouble(latitudeStr);
                    double longitude = Double.parseDouble(longitudeStr);
                    coordinatesByPostalCode.putIfAbsent(postalCode, new Coordinates(latitude, longitude));
                    loaded++;
                } catch (NumberFormatException e) {
                    log.warn("[CoordinatesLookup] Coordonnées invalides pour le code postal {} : lat={}, lon={}",
                            postalCode, latitudeStr, longitudeStr);
                }
            }

            log.info("[CoordinatesLookup] {} codes postaux chargés.", loaded);

        } catch (Exception e) {
            log.error("[CoordinatesLookup] Échec du chargement du CSV : {}", e.getMessage());
        }
    }

    /**
     * Résout les coordonnées GPS à partir d'un code postal.
     *
     * @param postalCode code postal (ex: J7V0R2)
     * @return les coordonnées correspondantes, ou vide si introuvables
     */
    public Optional<Coordinates> resolve(String postalCode) {
        if (postalCode == null || postalCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(coordinatesByPostalCode.get(postalCode));
    }

    private String unquote(String value) {
        return value.trim().replaceAll("^\"|\"$", "");
    }
}

