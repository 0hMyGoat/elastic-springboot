# 🚀 Elastic Springboot - Projet Spring Boot avec Elasticsearch et MySQL

Un projet Spring Boot entièrement configuré avec Docker Compose pour intégrer **Elasticsearch** (moteur de recherche) et **MySQL** (base de données), avec **MapStruct** pour le mappage d'objets.

## 📋 Table des Matières

- [🎯 Vue d'ensemble](#-vue-densemble)
- [⚙️ Prérequis](#-prérequis)
- [🚀 Démarrage Rapide](#-démarrage-rapide)
- [📁 Structure du Projet](#-structure-du-projet)
- [📖 Documentation](#-documentation)
- [🛠️ Technologies](#-technologies)
- [💻 Développement](#-développement)

## 🎯 Vue d'ensemble

Ce projet fournit une base solide pour développer des applications Spring Boot modernes avec :

- **Elasticsearch** pour la recherche et l'analyse de texte
- **MySQL** pour la persistence des données
- **Spring Data JPA** pour l'accès ORM à la base de données
- **MapStruct** pour le mappage automatique entre entités et DTOs
- **Swagger UI** pour la documentation interactive des APIs
- **Docker Compose** pour l'orchestration des services

## ⚙️ Prérequis

- **Docker** (version 20.10+)
- **Docker Compose** (version 2.0+)
- **Java** 25 ou supérieur
- **Maven** 3.8+
- **Git** (optionnel)

### Installation des Prérequis

#### Windows
```powershell
# Vérifier les installations
java -version
mvn -version
docker --version
docker-compose --version
```

#### Linux/Mac
```bash
# Vérifier les installations
java -version
mvn -version
docker --version
docker-compose --version
```

## 🚀 Démarrage Rapide

### 1️⃣ Cloner ou Télécharger le Projet

```bash
git clone <repository-url>
cd elastic-springboot
```

### 2️⃣ Démarrer les Services Docker

```powershell
# Windows
.\manage-docker.bat start

# Linux/Mac
./manage-docker.sh start

# Ou avec docker-compose directement
docker-compose up -d
```

Vérifiez que les services sont actifs :
```bash
docker-compose ps
```

**Résultat attendu:**
```
NAME                      STATUS
elasticsearch-springboot  Up (healthy)
mysql-springboot         Up (healthy)
```

### 3️⃣ Compiler l'Application

```bash
mvn clean compile
```

### 4️⃣ Démarrer l'Application Spring Boot

```bash
mvn spring-boot:run
```

Vous verrez :
```
Tomcat started on port(s): 8080 (http)
Started ElasticSpringbootApplication in X.XXX seconds
```

### 5️⃣ Accéder à l'Application

Ouvrez votre navigateur :

- **Swagger UI** : http://localhost:8080/api/swagger-ui.html
- **Health Check** : http://localhost:8080/api/actuator/health
- **Elasticsearch** : http://localhost:9200
- **MySQL Workbench** : localhost:3306 (user: springboot)

## 📁 Structure du Projet

```
elastic-springboot/
├── src/
│   ├── main/
│   │   ├── java/fr/octocorn/elasticspringboot/
│   │   │   ├── ElasticSpringbootApplication.java
│   │   │   ├── config/
│   │   │   │   └── ElasticsearchTestConfig.java
│   │   │   └── example/
│   │   │       └── MapStructExample.java
│   │   └── resources/
│   │       ├── application.yaml          # Configuration
│   │       └── application.properties
│   └── test/
│       └── java/...
├── docker-compose.yml                   # Configuration Docker
├── pom.xml                              # Dépendances Maven
├── manage-docker.bat                    # Script de gestion Docker (Windows)
├── manage-docker.sh                     # Script de gestion Docker (Linux/Mac)
├── README.md                            # Ce fichier
├── DOCKER_SETUP.md                      # Guide Docker détaillé
├── SETUP_GUIDE.md                       # Guide de configuration complet
├── QUICK_REFERENCE.md                   # Référence rapide des commandes
├── MAPSTRUCT_GUIDE.md                   # Guide MapStruct complet
└── MODIFICATIONS_SUMMARY.md             # Résumé des modifications
```

## 📖 Documentation

La documentation complète est divisée en plusieurs fichiers pour plus de clarté :

| Document | Contenu |
|----------|---------|
| **DOCKER_SETUP.md** | Instructions complètes pour Docker |
| **SETUP_GUIDE.md** | Configuration du projet et exemples pratiques |
| **QUICK_REFERENCE.md** | Commandes de test et dépannage rapide |
| **MAPSTRUCT_GUIDE.md** | Guide d'utilisation de MapStruct 1.6.0 LTS |
| **MODIFICATIONS_SUMMARY.md** | Résumé détaillé des modifications apportées |

## 🛠️ Technologies

### Dependencies Principales

| Technologie | Version | Rôle |
|-------------|---------|------|
| Spring Boot | 4.0.5 | Framework principal |
| Elasticsearch | 8.13.0 | Moteur de recherche |
| MySQL | 8.0 | Base de données |
| Spring Data JPA | Auto | ORM |
| Spring Data Elasticsearch | Auto | Elasticsearch Repository |
| MapStruct | **1.6.0 LTS** | Mappers |
| Lombok | Auto | Réduction de boilerplate |
| SpringDoc OpenAPI | 2.0.4 | Documentation Swagger |

### Ports Utilisés

| Service | Port | URL |
|---------|------|-----|
| Spring Boot | 8080 | http://localhost:8080/api |
| Elasticsearch | 9200 | http://localhost:9200 |
| MySQL | 3306 | localhost:3306 |

## 💻 Développement

### Créer Votre Première Entité

Consultez [SETUP_GUIDE.md](SETUP_GUIDE.md#-créer-votre-première-entité) pour un exemple complet incluant :
- Création d'une Entity JPA
- Création d'un Document Elasticsearch
- Création d'un DTO
- Création d'un Mapper MapStruct
- Création des Repositories
- Création d'un Service
- Création d'un Controller REST

### Exemple Rapide - Mapper avec MapStruct

```java
// Interface Mapper (MapStruct génère l'implémentation)
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);
}

// Utilisation dans un service
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository repository;
    
    public UserDTO createUser(UserDTO dto) {
        User user = mapper.toEntity(dto);
        user = repository.save(user);
        return mapper.toDTO(user);
    }
}
```

### Commandes Utiles

```bash
# Compiler
mvn clean compile

# Compiler et tester
mvn clean test

# Compiler et packager
mvn clean package

# Démarrer l'application
mvn spring-boot:run

# Voir les logs Elasticsearch
docker-compose logs -f elasticsearch

# Voir les logs MySQL
docker-compose logs -f mysql

# Arrêter les services
docker-compose down

# Arrêter et réinitialiser les données
docker-compose down -v
```

## 🔍 Vérification Installation

Pour vérifier que tout est configuré correctement :

```bash
# Elasticsearch accessible ?
curl http://localhost:9200

# MySQL accessible ?
mysql -h 127.0.0.1 -u springboot -pspringboot_password elastic_db

# Application démarrée ?
curl http://localhost:8080/api/actuator/health

# Swagger accessible ?
# Ouvrir http://localhost:8080/api/swagger-ui.html
```

## 🐛 Dépannage

### Les services Docker ne démarrent pas

```bash
# Vérifier l'état
docker-compose ps

# Voir les logs
docker-compose logs

# Redémarrer
docker-compose down -v
docker-compose up -d
```

### Erreur de compilation MapStruct

```bash
# Nettoyer et recompiler
mvn clean compile

# Vérifier la configuration
mvn validate
```

### Port déjà utilisé

```powershell
# Trouver le processus utilisant le port
netstat -ano | findstr :9200  # Elasticsearch
netstat -ano | findstr :3306  # MySQL
netstat -ano | findstr :8080  # Spring Boot

# Libérer le port ou modifier docker-compose.yml
```

## 📚 Ressources Supplémentaires

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data Elasticsearch](https://spring.io/projects/spring-data-elasticsearch)
- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [MapStruct Documentation](https://mapstruct.org/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

## 🤝 Contribution

Pour contribuer au projet :
1. Créer une branche (`git checkout -b feature/amazing-feature`)
2. Commiter les changements (`git commit -m 'Add amazing feature'`)
3. Pousser la branche (`git push origin feature/amazing-feature`)
4. Ouvrir une Pull Request

## 📄 Licence

Ce projet est fourni à titre d'exemple. Consultez le fichier LICENSE pour plus de détails.

## ✅ Checklist Post-Installation

Après le premier démarrage, vérifiez :

- [ ] Docker Compose en cours d'exécution (`docker-compose ps`)
- [ ] Elasticsearch accessible (http://localhost:9200)
- [ ] MySQL accessible (user: springboot)
- [ ] Application Spring Boot démarrée (port 8080)
- [ ] Swagger UI accessible
- [ ] Données MySQL persistantes
- [ ] Elasticsearch en créant un premier index

## 💡 Conseils Importants

✅ **MapStruct** : Version LTS 1.6.0 pour la stabilité long-terme  
✅ **Elasticsearch** : Single-node mode pour le développement  
✅ **MySQL** : DDL-auto en mode `update` (crée/modifie, ne supprime pas)  
✅ **Logs** : DEBUG pour Elasticsearch et Spring Data  
✅ **Persistance** : Données conservées via volumes Docker  

## 🎓 Prochaines Étapes

1. Lire [SETUP_GUIDE.md](SETUP_GUIDE.md) pour comprendre la structure
2. Consulter [MAPSTRUCT_GUIDE.md](MAPSTRUCT_GUIDE.md) pour utiliser les mappers
3. Lire [QUICK_REFERENCE.md](QUICK_REFERENCE.md) pour les commandes utiles
4. Créer vos entités et APIs
5. Tester avec Swagger UI

## 📞 Support

En cas de problème :
1. Consultez le fichier README correspondant au problème
2. Vérifiez les logs : `docker-compose logs`
3. Vérifiez la configuration : `docker-compose ps`
4. Réinitialisez si nécessaire : `docker-compose down -v && docker-compose up -d`

---

**Bon développement ! 🚀**

*Créé avec ❤️ pour faciliter le développement Spring Boot moderne*

