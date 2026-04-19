# 🧪 Commandes de Test et Vérification

## 🐳 Vérifier Docker Compose

```powershell
# Vérifier que Docker est disponible
docker --version
docker-compose --version

# Vérifier que les services sont actifs
docker-compose ps

# Voir les logs en temps réel
docker-compose logs -f

# Voir les logs d'un service spécifique
docker-compose logs -f elasticsearch
docker-compose logs -f mysql
```

## 🔍 Tester Elasticsearch

```bash
# Vérifier l'état général d'Elasticsearch
curl http://localhost:9200

# Affichage formaté
curl -X GET "http://localhost:9200" | Format-List

# Voir la version
curl http://localhost:9200/

# Lister tous les indices
curl http://localhost:9200/_cat/indices

# Créer un index de test
curl -X PUT "http://localhost:9200/test-index"

# Ajouter un document
curl -X POST "http://localhost:9200/test-index/_doc/1" -H "Content-Type: application/json" -d '{"name": "Test", "value": 42}'

# Récupérer le document
curl http://localhost:9200/test-index/_doc/1

# Rechercher dans l'index
curl "http://localhost:9200/test-index/_search"

# Supprimer l'index
curl -X DELETE "http://localhost:9200/test-index"
```

## 📦 Tester MySQL

```bash
# Se connecter à MySQL
mysql -h 127.0.0.1 -u springboot -pspringboot_password elastic_db

# Depuis le conteneur
docker exec -it mysql-springboot mysql -u springboot -pspringboot_password elastic_db

# Afficher les bases
SHOW DATABASES;

# Utiliser la base de données
USE elastic_db;

# Afficher les tables
SHOW TABLES;

# Afficher la structure d'une table
DESCRIBE products;

# Voir quelques lignes
SELECT * FROM products LIMIT 10;
```

## 🛠️ Compiler le Projet

```powershell
# Nettoyer et compiler
mvn clean compile

# Compiler et run les tests
mvn clean test

# Compiler et packager
mvn clean package

# Compiler sans les tests
mvn clean compile -DskipTests

# Compiler avec logs détaillés
mvn clean compile -X
```

## 🚀 Démarrer l'Application

```powershell
# Avec Spring Boot Maven Plugin
mvn spring-boot:run

# Avec version complète
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"

# Compiler et lancer un JAR
mvn clean package
java -jar target/elastic-springboot-0.0.1-SNAPSHOT.jar

# Avec variables d'environnement
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/elastic_db"
$env:SPRING_ELASTICSEARCH_REST_URIS="http://localhost:9200"
mvn spring-boot:run
```

## 🌐 Tester l'Application

```bash
# Health Check
curl http://localhost:8080/api/actuator/health | Format-List

# Swagger UI
http://localhost:8080/api/swagger-ui.html

# API Docs (JSON)
curl http://localhost:8080/api/v3/api-docs | Format-List

# Metrics
curl http://localhost:8080/api/actuator/metrics | Format-List

# Info de l'application
curl http://localhost:8080/api/actuator/info | Format-List
```

## 📊 Vérifier la Connexion Elasticsearch

```java
// Dans un service Spring
@Service
public class ElasticsearchTest {
    private final RestHighLevelClient client;
    
    public void testConnection() {
        try {
            MainResponse response = client.info(RequestOptions.DEFAULT);
            System.out.println("Connected to Elasticsearch: " + response.getVersion().getNumber());
        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
        }
    }
}
```

## 📊 Vérifier la Connexion MySQL

```java
// Dans un service Spring
@Service
public class DatabaseTest {
    @Autowired
    private DataSource dataSource;
    
    public void testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT VERSION()";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("MySQL Version: " + rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}
```

## 🧹 Nettoyer et Redémarrer

```powershell
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (réinitialisation complète)
docker-compose down -v

# Supprimer les images (libération d'espace)
docker-compose down -v --rmi all

# Nettoyer le projet Maven
mvn clean

# Nettoyer les caches Maven
mvn clean -U

# Redémarrer tout
docker-compose up -d
mvn clean compile
mvn spring-boot:run
```

## 🔧 Troubleshooting Rapide

```powershell
# Les services démarrent-ils ?
docker-compose ps

# Voir les erreurs de démarrage
docker-compose logs

# Port déjà utilisé ?
netstat -ano | findstr :9200
netstat -ano | findstr :3306
netstat -ano | findstr :8080

# Le JAR est-il construit ?
Test-Path target/elastic-springboot-0.0.1-SNAPSHOT.jar

# Vérifier la compilation
mvn validate

# Réapliquer la configuration
mvn clean -U compile

# Reconstruire entièrement
mvn clean install -DskipTests
```

## 📈 Monitoring

```bash
# Voir les metrics de Elasticsearch
curl http://localhost:9200/_stats | Format-List

# Voir l'utilisation des indices
curl http://localhost:9200/_cat/indices?v

# Voir les noeuds
curl http://localhost:9200/_cat/nodes?v

# Voir la santé du cluster
curl http://localhost:9200/_cluster/health?pretty

# Voir les shards
curl http://localhost:9200/_cat/shards
```

## 🐛 Logs Détaillés

```powershell
# Voir les logs Spring Boot
$logs = docker-compose logs --tail=100 | Select-Object

# Voir les logs MySQL
docker-compose logs -f mysql

# Voir les logs Elasticsearch
docker-compose logs -f elasticsearch

# Voir tous les logs avec timestamps
docker-compose logs --timestamps
```

## 🔐 Accès aux Services

```
Elasticsearch Admin:
- URL: http://localhost:9200/_plugin/kibana/ (si Kibana installé)
- Direct: http://localhost:9200/

MySQL Workbench:
- Host: 127.0.0.1
- Port: 3306
- User: springboot
- Password: springboot_password
- Database: elastic_db

Application Swagger:
- URL: http://localhost:8080/api/swagger-ui.html
- API JSON: http://localhost:8080/api/v3/api-docs
```

## ✅ Checklist de Démarrage

- [ ] `docker-compose ps` - Tous les services UP
- [ ] `curl http://localhost:9200` - Elasticsearch répond
- [ ] `mysql -h 127.0.0.1 -u springboot -p elastic_db` - MySQL accessible
- [ ] `mvn clean compile -DskipTests` - Compilation OK
- [ ] `mvn spring-boot:run` - Application démarre
- [ ] `curl http://localhost:8080/api/actuator/health` - Application UP
- [ ] Navigateur: `http://localhost:8080/api/swagger-ui.html` - Swagger accessi
ble

