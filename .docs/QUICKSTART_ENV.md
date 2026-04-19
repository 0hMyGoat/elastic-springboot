# 🚀 Démarrage Rapide avec Variables d'Environnement

Guide rapide pour démarrer le projet avec les variables d'environnement.

## 1️⃣ Premier Démarrage

### Étape 1 : Créer le fichier `.env`
```bash
# Copier le fichier exemple
cp .env.example .env

# Le fichier .env local est maintenant créé avec les valeurs par défaut
```

### Étape 2 : Vérifier que .env est ignoré
```bash
# Vérifier que .env est dans .gitignore
cat .gitignore | grep .env
```

**Résultat attendu :** `.env` apparaît dans la liste

### Étape 3 : Démarrer les services Docker
```bash
# Charger les variables et démarrer
docker-compose up -d

# Vérifier que les services sont actifs
docker-compose ps
```

**Services attendus :**
```
NAME                      STATUS
elasticsearch-springboot  Up (healthy)
kibana-springboot         Up (healthy)
mysql-springboot          Up (healthy)
```

### Étape 4 : Vérifier les variables chargées
```bash
# Afficher la configuration avec les variables interpolées
docker-compose config

# Vérifier Elasticsearch
curl http://localhost:9200

# Vérifier Kibana
curl http://localhost:5601/api/status

# Vérifier MySQL
mysql -h localhost -u springboot -p elastic_db
# Entrez: springboot_password
```

### Étape 5 : Démarrer l'application Spring Boot
```bash
# Compiler
mvn clean compile

# Démarrer
mvn spring-boot:run

# Vérifier que l'application est prête
curl http://localhost:8080/api/actuator/health
```

## 2️⃣ Personnaliser les Variables

### Modifier un port (exemple)
```env
# .env
ES_PORT=19200          # Elasticsearch sur 19200 au lieu de 9200
KIBANA_PORT=15601      # Kibana sur 15601 au lieu de 5601
MYSQL_PORT=13306       # MySQL sur 13306 au lieu de 3306
```

### Modifier les identifiants
```env
# .env
ELASTIC_USERNAME=mon_user
ELASTIC_PASSWORD=mon_password_secure
MYSQL_USER=mon_app
MYSQL_PASSWORD=mon_password_secure
```

### Redémarrer après modification
```bash
# Arrêter les services
docker-compose down

# Redémarrer avec les nouvelles variables
docker-compose up -d

# Vérifier les changements
docker-compose config
```

## 3️⃣ Accès aux Services

### Elasticsearch
```bash
# URL
http://localhost:9200 (ou ${ES_PORT})

# Vérifier
curl http://localhost:${ES_PORT:-9200}
```

### Kibana
```bash
# URL
http://localhost:5601 (ou ${KIBANA_PORT})

# Ouvrir dans le navigateur
http://localhost:${KIBANA_PORT:-5601}
```

### MySQL
```bash
# Ligne de commande
mysql -h localhost -P ${MYSQL_PORT:-3306} -u ${MYSQL_USER:-springboot} -p ${MYSQL_DATABASE:-elastic_db}

# Entrez le password: ${MYSQL_PASSWORD:-springboot_password}
```

### Spring Boot Application
```bash
# URL
http://localhost:8080/api

# Swagger UI
http://localhost:8080/api/swagger-ui.html

# Health Check
http://localhost:8080/api/actuator/health
```

## 4️⃣ Commandes Utiles

```bash
# Voir la configuration avec variables interpolées
docker-compose config

# Voir les logs de tous les services
docker-compose logs -f

# Voir les logs d'un service spécifique
docker-compose logs -f elasticsearch-springboot
docker-compose logs -f kibana-springboot
docker-compose logs -f mysql-springboot

# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (réinitialisation complète)
docker-compose down -v

# Redémarrer les services
docker-compose restart

# Vérifier l'état
docker-compose ps

# Exécuter une commande dans un container
docker-compose exec elasticsearch-springboot curl -s http://localhost:9200
```

## 5️⃣ Vérification Complète

### Checklist d'Installation
```bash
#!/bin/bash

echo "✓ Vérification des fichiers"
[ -f .env ] && echo "  ✅ .env existe" || echo "  ❌ .env manquant"
[ -f .env.example ] && echo "  ✅ .env.example existe" || echo "  ❌ .env.example manquant"

echo "✓ Vérification Docker"
docker-compose ps | grep "Up (healthy)" > /dev/null && echo "  ✅ Tous les services sont healthy" || echo "  ❌ Services unhealthy"

echo "✓ Vérification Elasticsearch"
curl -s http://localhost:9200 > /dev/null && echo "  ✅ Elasticsearch accessible" || echo "  ❌ Elasticsearch inaccessible"

echo "✓ Vérification Kibana"
curl -s http://localhost:5601/api/status > /dev/null && echo "  ✅ Kibana accessible" || echo "  ❌ Kibana inaccessible"

echo "✓ Vérification MySQL"
mysql -h localhost -u springboot -pspringboot_password elastic_db -e "SELECT 1" > /dev/null 2>&1 && echo "  ✅ MySQL accessible" || echo "  ❌ MySQL inaccessible"

echo "✓ Vérification Application Spring Boot"
curl -s http://localhost:8080/api/actuator/health > /dev/null && echo "  ✅ Application accessible" || echo "  ❌ Application inaccessible"

echo ""
echo "🎉 Vérification complète!"
```

## 6️⃣ Dépannage

### Docker Compose charge-t-il le .env ?
```bash
# Vérifier que docker-compose charge le fichier
docker-compose config | grep ES_PORT

# Ou vérifier les valeurs réelles dans le container
docker inspect elasticsearch-springboot | grep -A 5 '"Env"'
```

### Les variables ne sont pas chargées
```bash
# 1. Vérifier que le fichier .env existe
ls -la .env

# 2. Vérifier le format du fichier (pas d'espaces autour du =)
cat .env

# 3. Redémarrer Docker Compose
docker-compose down
docker-compose up -d

# 4. Vérifier la configuration
docker-compose config
```

### Erreur de connexion MySQL
```bash
# Vérifier que le password du fichier .env correspond
cat .env | grep MYSQL_PASSWORD

# Tester la connexion directement
mysql -h localhost -u springboot -p elastic_db

# Voir les logs MySQL
docker-compose logs mysql-springboot
```

### Erreur Elasticsearch
```bash
# Vérifier les logs
docker-compose logs elasticsearch-springboot

# Vérifier la mémoire disponible
docker stats elasticsearch-springboot

# Vérifier l'accès
curl http://localhost:${ES_PORT:-9200}
```

## 7️⃣ Gitignore

Vérifiez que `.env` est bien ignoré :

```bash
# .gitignore devrait contenir:
.env
.env.local

# Vérifier
cat .gitignore | grep .env
```

## 8️⃣ Équipe

### Pour partager le projet
1. **Committer** `.env.example` avec les variables
2. **NE PAS committer** `.env` (contient mots de passe)
3. Chaque développeur fait : `cp .env.example .env`
4. Chacun personnalise son `.env` localement

### Documentation
```bash
# Pour documenter les variables
cat ENV_VARIABLES.md
```

## ✅ Résumé des Étapes

```bash
# 1. Créer le .env
cp .env.example .env

# 2. Démarrer Docker
docker-compose up -d

# 3. Compiler l'application
mvn clean compile

# 4. Démarrer l'application
mvn spring-boot:run

# 5. Accéder
# Swagger: http://localhost:8080/api/swagger-ui.html
# Kibana: http://localhost:5601
# MySQL: localhost:3306
# Elasticsearch: http://localhost:9200
```

**Temps total : ~5 minutes ⚡**

