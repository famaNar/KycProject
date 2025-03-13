# Système KYC (Know Your Customer)

Ce projet implémente un système KYC (Know Your Customer) pour une plateforme financière, permettant l'inscription des clients, la soumission de formulaires KYC, le téléchargement de documents, la vérification faciale et les notifications par email.

## Architecture du projet

Le système est basé sur une architecture microservices comprenant :

1. **Customer Service** (Port 8081)
   - Gestion des clients et de leurs informations personnelles
   - Traitement des formulaires KYC
   - Gestion des revenus des clients
   - Gestion des comptes des clients
   - Publication d'événements via Kafka

2. **Document Service** (Port 8082)
   - Gestion des documents d'identité (carte d'identité, passeport, etc.)
   - Vérification faciale (comparaison photo ID vs selfie)
   - Stockage des documents

3. **Notification Service** (Port 8083)
   - Écoute des événements Kafka
   - Envoi d'emails de notification aux clients
   - Templates d'emails personnalisés

## Prérequis

- Java 17
- Maven
- Docker et Docker Compose (pour Kafka)

## Installation et démarrage

### 1. Démarrer Kafka et Zookeeper

```bash
docker-compose up -d
```

Cela lancera :
- Zookeeper sur le port 2181
- Kafka sur le port 9092
- Kafka UI sur le port 8080 (interface web pour visualiser les topics Kafka)

### 2. Compiler et démarrer les microservices

Ouvrez trois terminaux différents pour lancer chaque service :

**Customer Service**
```bash
cd customer-service
mvn clean install
mvn spring-boot:run
```

**Document Service**
```bash
cd document-service
mvn clean install
mvn spring-boot:run
```

**Notification Service**
```bash
cd notification-service
mvn clean install
mvn spring-boot:run
```

## Flux du processus KYC

1. **Inscription du client**
   - Le client s'inscrit via l'API du Customer Service
   - Un événement CLIENT_REGISTERED est publié sur Kafka
   - Le Notification Service envoie un email de bienvenue avec un lien vers le formulaire KYC

2. **Soumission du formulaire KYC**
   - Le client remplit le formulaire avec ses informations personnelles et financières
   - Un événement KYC_FORM_SUBMITTED est publié
   - Le Notification Service envoie un email de confirmation

3. **Téléchargement de documents**
   - Le client télécharge ses documents d'identité via le Document Service
   - Les documents sont stockés et associés au client

4. **Vérification faciale**
   - Le client soumet une photo selfie
   - Le Document Service compare la photo avec celle du document d'identité
   - Le résultat de la vérification est enregistré

5. **Approbation/Rejet du KYC**
   - Un administrateur vérifie les informations et documents
   - Le statut KYC est mis à jour (APPROUVE ou REJETE)
   - Un événement KYC_APPROVED ou KYC_REJECTED est publié
   - Le Notification Service envoie un email de notification approprié

## API Endpoints

### Customer Service (8081)

- `POST /api/clients` : Inscription d'un nouveau client
- `GET /api/clients/{id}` : Récupération des informations d'un client
- `PUT /api/clients/{id}/kyc` : Mise à jour du statut KYC
- `POST /api/revenues` : Ajout d'informations financières

### Document Service (8082)

- `POST /api/documents/upload` : Téléchargement d'un document
- `GET /api/documents/client/{clientId}` : Liste des documents d'un client
- `POST /api/facial-verification/verify` : Vérification faciale
- `GET /api/facial-verification/client/{clientId}/status` : Statut de la vérification faciale

### Notification Service (8083)

- `POST /api/notifications/test/welcome` : Test d'envoi d'email de bienvenue
- `POST /api/notifications/test/kyc-submitted` : Test d'envoi d'email de soumission KYC
- `POST /api/notifications/test/kyc-approved` : Test d'envoi d'email d'approbation KYC
- `POST /api/notifications/test/kyc-rejected` : Test d'envoi d'email de rejet KYC

## Sécurité

Cette version initiale se concentre sur les fonctionnalités de base sans implémentation complexe de sécurité. Dans une version de production, il faudrait ajouter :

- Authentification OAuth2/JWT
- Chiffrement des données sensibles
- HTTPS pour toutes les communications
- Validation approfondie des entrées
- Gestion des rôles et permissions
