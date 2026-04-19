# Elastic Springboot - Configuration Docker Compose

## 📋 Description

Ce projet Spring Boot intègre :
- **Elasticsearch** : Moteur de recherche et d'analyse distribuée
- **MySQL** : Base de données relationnelle
- **MapStruct** : Mappage d'objets entre entités et DTO

## 🚀 Démarrer l'application

### 1. Démarrer les services Docker

```bash
docker-compose up -d
```

Cela va créer et démarrer :
- **Elasticsearch** (http://localhost:9200)
- **MySQL** (localhost:3306)

### 2. Vérifier les services

```bash
# Vérifier Elasticsearch
curl http://localhost:9200

# Vérifier MySQL
mysql -h 127.0.0.1 -u springboot -p elastic_db
# Mot de passe : springboot_password
```

### 3. Compiler et démarrer l'application Spring Boot

```bash
mvn clean package
mvn spring-boot:run
```

L'application sera accessible à : `http://localhost:8080/api`

La documentation Swagger : `http://localhost:8080/api/swagger-ui.html`

## 📝 Configuration

### application.yaml

Les paramètres de connexion sont configurés pour Docker Compose :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/elastic_db
    username: springboot
    password: springboot_password
  
  elasticsearch:
    rest:
      uris: http://localhost:9200
```

### docker-compose.yml

Services disponibles :
- **elasticsearch** : Single-node Elasticsearch 8.13.0 (port 9200)
- **mysql** : MySQL 8.0 (port 3306)

**Identifiants MySQL :**
- Utilisateur : `springboot`
- Mot de passe : `springboot_password`
- Base de données : `elastic_db`
- Root password : `root`

## 🛑 Arrêter les services

```bash
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

## 📦 Dépendances principales

- Spring Boot 4.0.5
- Spring Data Elasticsearch
- Spring Data JPA
- MySQL Connector
- MapStruct 1.6.0
- Lombok
- SpringDoc OpenAPI (Swagger)

## 🔧 Développement

### MapStruct

Pour créer des mappers entre entités :

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User entity);
    User toEntity(UserDTO dto);
}
```

Injectez le mapper dans vos services :

```java
@Service
public class UserService {
    private final UserMapper mapper;
    
    public UserService(UserMapper mapper) {
        this.mapper = mapper;
    }
}
```

## 📊 Logs

Les logs détaillés pour Spring Data et Elasticsearch sont configurés en DEBUG pour faciliter le développement.

## 🐛 Dépannage

Si vous rencontrez des problèmes de connexion :

1. Vérifiez que les conteneurs sont en cours d'exécution :
   ```bash
   docker ps
   ```

2. Vérifiez les logs des conteneurs :
   ```bash
   docker logs elasticsearch-springboot
   docker logs mysql-springboot
   ```

3. Assurez-vous que les ports 9200, 3306 et 8080 ne sont pas utilisés par d'autres applications.

