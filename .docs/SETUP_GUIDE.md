# 📋 Configuration et Structure du Projet

## 🎯 Objectif
Ce projet Spring Boot intègre Elasticsearch pour la recherche et l'analyse, MySQL pour la persistence, et MapStruct pour le mappage d'objets.

## 📂 Structure des Fichiers Créés

```
elastic-springboot/
├── docker-compose.yml                 # Configuration Docker (Elasticsearch + MySQL)
├── DOCKER_SETUP.md                    # Guide complet d'utilisation Docker
├── manage-docker.bat                  # Script Windows pour gérer Docker
├── manage-docker.sh                   # Script Linux/Mac pour gérer Docker
├── pom.xml                            # Dépendances Maven mises à jour
├── src/
│   └── main/
│       ├── java/
│       │   └── fr/octocorn/elasticspringboot/
│       │       ├── ElasticSpringbootApplication.java
│       │       └── example/
│       │           └── MapStructExample.java
│       └── resources/
│           ├── application.yaml       # Configuration Spring
│           └── application.properties # (maintenant vide, config en YAML)
```

## 🔧 Configuration Effectuée

### 1. **docker-compose.yml**
- **Elasticsearch 8.13.0** : Service de recherche sur le port 9200
- **MySQL 8.0** : Base de données sur le port 3306
- Volumes persistants pour les données
- Health checks pour vérifier la disponibilité

### 2. **application.yaml**
```yaml
Configurations principales:
- Connexion Elasticsearch: http://localhost:9200
- Connexion MySQL: jdbc:mysql://localhost:3306/elastic_db
- User MySQL: springboot / springboot_password
- Hibernate DDL: update (création automatique des tables)
- Logs DEBUG pour Elasticsearch et Spring Data
```

### 3. **pom.xml - Dépendances Ajoutées/Mises à Jour**

| Dépendance | Version | Raison |
|-----------|---------|--------|
| spring-boot-starter-data-elasticsearch | Auto | Intégration Elasticsearch |
| spring-boot-starter-data-jpa | Auto | Persistence en base |
| spring-boot-starter-web | Auto | API REST |
| mysql-connector-j | Auto | Driver MySQL |
| mapstruct | 1.6.0 LTS | Mappage d'objets |
| mapstruct-processor | 1.6.0 | Compilation des mappers |
| springdoc-openapi-starter-webmvc-ui | 2.0.4 | Swagger UI |

### 4. **Configuration du Compilateur Maven**
- Intégration des annotation processors pour:
  - Lombok (annotations @Data, @Getter, @Setter, etc.)
  - MapStruct (génération automatique des mappers)

## 🚀 Démarrage Rapide

### Étape 1 : Démarrer les services
```powershell
# Windows
.\manage-docker.bat start

# Linux/Mac
./manage-docker.sh start
```

### Étape 2 : Compiler l'application
```bash
mvn clean compile
```

### Étape 3 : Démarrer Spring Boot
```bash
mvn spring-boot:run
```

### Étape 4 : Tester
- **Swagger UI** : http://localhost:8080/api/swagger-ui.html
- **Health Check** : http://localhost:8080/api/actuator/health
- **Elasticsearch** : http://localhost:9200
- **MySQL** : localhost:3306

## 💻 Vérification des Services

```powershell
# Vérifier l'état
docker-compose ps

# Voir les logs
docker-compose logs -f

# Tester Elasticsearch
curl http://localhost:9200

# Tester MySQL
mysql -h 127.0.0.1 -u springboot -pspringboot_password elastic_db
```

## 📝 Créer votre première Entité

### 1. Créer une Entity JPA
```java
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Double price;
}
```

### 2. Créer un Document Elasticsearch
```java
@Document(indexName = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {
    @Id
    private Long id;
    
    @Field(type = FieldType.Text)
    private String name;
    
    @Field(type = FieldType.Text)
    private String description;
    
    @Field(type = FieldType.Double)
    private Double price;
}
```

### 3. Créer un DTO
```java
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
}
```

### 4. Créer un Mapper MapStruct
```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO dto);
    ProductDTO documentToDTO(ProductDocument doc);
    ProductDocument dtoToDocument(ProductDTO dto);
}
```

### 5. Créer les Repositories
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
}

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
    List<ProductDocument> findByNameContaining(String name);
}
```

### 6. Créer un Service
```java
@Service
public class ProductService {
    private final ProductRepository repository;
    private final ProductSearchRepository searchRepository;
    private final ProductMapper mapper;
    
    public ProductService(ProductRepository repository, 
                         ProductSearchRepository searchRepository,
                         ProductMapper mapper) {
        this.repository = repository;
        this.searchRepository = searchRepository;
        this.mapper = mapper;
    }
    
    public ProductDTO create(ProductDTO dto) {
        Product product = mapper.toEntity(dto);
        product = repository.save(product);
        searchRepository.save(mapper.dtoToDocument(mapper.toDTO(product)));
        return mapper.toDTO(product);
    }
    
    public List<ProductDTO> search(String keyword) {
        return searchRepository.findByNameContaining(keyword)
            .stream()
            .map(mapper::documentToDTO)
            .collect(Collectors.toList());
    }
}
```

### 7. Créer un Controller REST
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }
}
```

## 🧹 Nettoyer les Services

```bash
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (réinitialiser)
docker-compose down -v
```

## 🔍 Dépannage

### Elasticsearch ne démarre pas
```bash
# Vérifier les logs
docker logs elasticsearch-springboot

# Vérifier la disponibilité du port 9200
netstat -ano | findstr :9200
```

### Erreur de connexion MySQL
```bash
# Vérifier les logs
docker logs mysql-springboot

# Se connecter à MySQL
docker exec -it mysql-springboot mysql -uroot -proot
```

### Erreur de compilation MapStruct
- Assurez-vous que `mapstruct-processor` est en scope `provided`
- Nettoyez et recompiler : `mvn clean compile`
- Vérifiez que l'interface mapper a l'annotation `@Mapper(componentModel = "spring")`

## 📚 Ressources Supplémentaires

- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Spring Data Elasticsearch](https://spring.io/projects/spring-data-elasticsearch)
- [MapStruct Documentation](https://mapstruct.org/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## ✅ Checklist de Démarrage

- [ ] Docker et Docker Compose installés
- [ ] Java 25+ installé
- [ ] Maven installé
- [ ] Port 9200 disponible (Elasticsearch)
- [ ] Port 3306 disponible (MySQL)
- [ ] Port 8080 disponible (Spring Boot)
- [ ] Démarrer les services Docker
- [ ] Compiler le projet
- [ ] Démarrer l'application
- [ ] Accéder à http://localhost:8080/api/swagger-ui.html

Bon développement! 🚀

