---
applyTo: '**/*.java'
description: 'Règles et bonnes pratiques pour les projets Spring Boot.'
---

# Guidelines Projet — Spring Boot

## Stack technique

- **Java 25 LTS**
- **Spring Boot** (dernière version LTS)
- **Elasticsearch** (dernière version LTS)
- **Lombok** pour la réduction du boilerplate
- **MapStruct** pour les conversions simples de modèles
- Toujours privilégier les versions **LTS** de chaque librairie/dépendance.

---

## Principes de conception

- **KISS** — Garder le code simple, éviter l'over-engineering.
- **SOLID** — Respecter les cinq principes fondamentaux.
- **YAGNI** — Ne pas implémenter ce qui n'est pas nécessaire maintenant.
- **Clean Code** — Lisibilité avant tout.
- **Clean Architecture** — Séparation stricte des responsabilités.

---

## Organisation des packages

Organiser **par domaine métier**, jamais par couche technique.

- Les **DTO** sont regroupés dans un sous-package `dto/`, avec des sous-répertoires si nécessaire. Suffixe : `DTO` (ex : `CommandeDTO`).
- Les **entités** sont regroupées dans un sous-package `entity/`. Suffixe : `Entity` (ex : `CommandeEntity`).
- Les **models**, **controllers**, **services** et **repositories** restent à la **racine** du package domaine.

```
com.example.monprojet
├── commande/
│   ├── CommandeController.java
│   ├── CommandeService.java
│   ├── CommandeRepository.java
│   ├── Commande.java              ← model métier
│   ├── adapter/
│   │   └──  CommandeAdapter.java
│   ├── entity/
│   │   └── CommandeEntity.java
│   └── dto/
│       ├── CommandeDTO.java
│       └── creation/
│           └── CreationCommandeDTO.java
├── produit/
│   ├── ProduitController.java
│   ├── ProduitService.java
│   ├── ProduitRepository.java
│   ├── Produit.java
│   ├── mapper/
│   │   └──  ProduitMapper.java
│   ├── entity/
│   │   └── ProduitEntity.java
│   └── dto/
│       └── ProduitDTO.java
└── client/
    └── ...
```

---

## Granularité : Entity, Model, DTO

- Pour un **CRUD simple**, il n'est pas toujours nécessaire de créer des DTO ou des Models. L'entité peut être utilisée directement à travers les couches.
- **Adapter la granularité à la complexité** du cas d'usage :
    - Cas simple (CRUD basique) → `Entity` suffit.
    - Cas intermédiaire (transformation légère) → `Entity` + `DTO`.
    - Cas complexe (logique métier riche) → `Entity` + `Model` + `DTO`.
- Sauf si une séparation complète est **explicitement demandée**.

---

## Conventions de nommage

- **Langue du code** : anglais, sauf pour les noms de domaine métier qui restent en **français** (ex : `Commande`, `Produit`, `Facture`).
- **Documentation** : toujours en **français**.
- Utiliser des **mots complets**, jamais d'abréviations (`commande` et non `cmd`, `repository` et non `repo`).
- Les DTO sont des **records** suffixés par `DTO` :

```java
public record CommandeDTO(Long identifiant, String reference, LocalDate dateCreation) {}
```

---

## Documentation

- Documenter **systématiquement** chaque méthode publique en **JavaDoc**.
- Détailler les paramètres (`@param`) et retours (`@return`) uniquement lorsque c'est **pertinent** (non trivial).

```java
/**
 * Recherche une commande par son identifiant.
 *
 * @param identifiant identifiant technique de la commande
 * @return la commande correspondante
 * @throws CommandeIntrouvableException si aucune commande ne correspond
 */
public Commande rechercherParIdentifiant(Long identifiant) { ... }
```

---

## Conversion de modèles

- **MapStruct** pour les conversions simples (entité ↔ DTO).
- **Design Pattern Adapter** pour les conversions complexes impliquant de la logique métier ou des appels externes.

---

## Accesseurs et logique métier

- Privilégier l'encapsulation de logique métier dans les **accesseurs** (getters enrichis) lorsque c'est pertinent, plutôt que de disperser cette logique dans les services.

```java
public BigDecimal getMontantTotalTtc() {
    return this.montantHorsTaxe.multiply(BigDecimal.ONE.add(this.tauxTva));
}
```

---

## API REST

- Respecter les normes **RESTful** : `ressources/{id}/sous-ressources`
- Exemples :
    - `GET /commandes` — liste des commandes
    - `GET /commandes/{id}` — détail d'une commande
    - `GET /commandes/{id}/lignes` — lignes d'une commande
    - `POST /commandes` — création d'une commande
- Utiliser un **Global Exception Handler** (`@RestControllerAdvice`) pour centraliser la gestion des erreurs REST.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommandeIntrouvableException.class)
    public ResponseEntity<ErreurDTO> gererCommandeIntrouvable(CommandeIntrouvableException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErreurDTO(exception.getMessage()));
    }
}
```

---
## Gestion des exceptions

- Favoriser les **exceptions personnalisées** pour les erreurs métier spécifiques.
- Utiliser des **exceptions standard** (ex : `IllegalArgumentException`, `IllegalStateException`) pour les erreurs techniques ou de validation génériques.
- Ne jamais utiliser d'exception générique (`Exception`, `RuntimeException`) sans raison valable.

---

## Formatage du code

- **Longueur maximale d'une méthode** : ~25 lignes (sauf exception justifiée).
- **Longueur maximale d'une ligne** : 120 caractères.
- Favoriser l'extraction de méthodes privées pour améliorer la lisibilité.

---

## Tests

- Les tests unitaires reflètent la **logique métier**, pas l'implémentation technique (approche TDD).
- Privilégier les **tests paramétrés** (`@ParameterizedTest`) pour éviter la duplication.
- Nommer les tests de manière explicite et orientée métier.

```java
@ParameterizedTest
@CsvSource({
    "100.00, 0.20, 120.00",
    "250.00, 0.10, 275.00"
})
void devrait_calculer_le_montant_ttc(BigDecimal montantHt, BigDecimal taux, BigDecimal attendu) {
    Commande commande = new Commande(montantHt, taux);
    assertThat(commande.getMontantTotalTtc()).isEqualByComparingTo(attendu);
}
```

---

## Sécurité

- ⚠️ **Si un enjeu de sécurité est détecté** (injection, exposition de données sensibles, faille XSS, CORS mal configuré…) : **le souligner explicitement**.
- 🔑 **Si un secret est détecté** (clé API, mot de passe, token en dur dans le code…) : **le souligner immédiatement**. Ne jamais committer de secret. Utiliser des variables d'environnement ou un gestionnaire de secrets.

---

## Règles pour Copilot / IA

- Faire des **réponses courtes** et aller à l'essentiel.
- Ne **pas générer de fichier `.md`** récapitulatif sauf si explicitement demandé.
- Toujours rédiger du code **simple à comprendre**.