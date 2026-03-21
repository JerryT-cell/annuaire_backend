# Annuaire Backend

EN: Spring Boot backend for the Banas de France directory and events platform. This project is intended for French speakers, so the rest of this README is written in French. It provides a JWT-secured REST API for authentication, member profiles, public directory access, event management, RSVP handling, and email subscriptions. From here it is french:

Backend Spring Boot pour l'annuaire des Banas de France. Le projet expose une API REST sécurisée par JWT pour :

- l'authentification et le renouvellement de token
- la gestion des profils et de l'annuaire public
- la gestion des événements et des RSVP 
- les abonnements email et l'envoi d'annonces admin

## Stack

- Java 22
- Spring Boot 4
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Mail
- Swagger / OpenAPI via `springdoc`

## Structure du projet

- `src/main/java/com/banafrance/annuaire/controller` : endpoints REST
- `src/main/java/com/banafrance/annuaire/service` : logique métier
- `src/main/java/com/banafrance/annuaire/repository` : accès base de données
- `src/main/java/com/banafrance/annuaire/model` : entités JPA et enums
- `src/main/resources/application.properties` : configuration locale par défaut
- `src/main/resources/application-test.properties` : profil `test`

## Endpoints principaux

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/password-reset/request`
- `POST /api/auth/password-reset/apply`
- `GET /api/directory`
- `GET /api/profiles/{id}`
- `PUT /api/profiles/me`
- `GET /api/events`
- `GET /api/events/{id}`
- `POST /api/events/{id}/rsvp`
- `POST /api/subscriptions`
- `DELETE /api/subscriptions/{email}`
- `GET /api/admin/subscriptions`
- `POST /api/admin/announcements`

Notes de sécurité :

- les endpoints `auth`, `GET /api/directory`, `GET /api/events`, `GET /api/events/{id}` et `POST /api/subscriptions` sont publics
- les routes `/api/admin/**` demandent le rôle `ADMIN`
- le reste nécessite une authentification JWT

## Configuration locale

La configuration par défaut attend :

- PostgreSQL sur `localhost:5433`
- base : `annuaire_db`
- utilisateur : `annuaire_user`
- mot de passe : `secret`
- serveur SMTP local sur `localhost:1025`
- application sur `http://localhost:8080`

Ces valeurs sont définies dans `src/main/resources/application.properties`.

## Comment démarrer

### 1. Préparer les dépendances locales

Lancer PostgreSQL avec une base accessible via :

```text
jdbc:postgresql://localhost:5433/annuaire_db
```

Puis démarrer un serveur SMTP local sur le port `1025` si vous voulez tester les emails.

### 2. Lancer l'application

Avec le wrapper Maven :

```bash
./mvnw spring-boot:run
```

Ou avec le profil `test` :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

Le profil `test` charge `application-test.properties` et désactive la sécurité standard via `TestSecurityConfig`, ce qui facilite les essais locaux.

### 3. Vérifier que l'API tourne

- Health check : `http://localhost:8080/actuator/health`
- Swagger UI : `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON : `http://localhost:8080/api-docs`

## Tests

Commande :

```bash
./mvnw test
```

À l'état actuel, les tests ne passent pas sans base PostgreSQL disponible sur `localhost:5433`, car le chargement du contexte Spring essaie de se connecter à la base configurée.
