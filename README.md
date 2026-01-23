# Parking Intelligent - Application de Gestion de Parking

## Membres du groupe
- Maryline FONKOU
- Junior FOKOU
- Tatiana Ndoumbeu

## Branche par defaut (stable)
- master

---

## Architecture Backend

### Architecture MVC (Model-View-Controller)

Le backend suit une architecture MVC stricte avec une separation claire des responsabilites :

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Frontend)                         │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                         CONTROLLERS                              │
│  - Recevoir les requetes HTTP                                   │
│  - Deleguer au Service                                          │
│  - Retourner la reponse HTTP                                    │
│  - PAS de logique metier                                        │
│  - PAS de try-catch (gere par GlobalExceptionHandler)           │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                          SERVICES                                │
│  - TOUTE la logique metier                                      │
│  - Validation des donnees                                       │
│  - Regles de gestion                                            │
│  - Mapping DTO <-> Entite                                       │
│  - Notifications WebSocket                                      │
│  - Lancement des exceptions metier                              │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        REPOSITORIES                              │
│  - Acces aux donnees (JPA/Hibernate)                            │
│  - Requetes personnalisees                                      │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      BASE DE DONNEES                             │
└─────────────────────────────────────────────────────────────────┘
```

### Structure des Packages

```
esiag.back/
├── controllers/          # Controllers REST (exposition HTTP uniquement)
│   ├── AuthController.java
│   ├── PersonneController.java
│   ├── PlaceController.java
│   ├── ReservationPlaceController.java
│   └── VehiculeController.java
│
├── services/             # Services metier (toute la logique)
│   ├── AuthService.java
│   ├── PersonneService.java
│   ├── PlaceService.java
│   ├── ReservationPlaceService.java
│   ├── VehiculeService.java
│   ├── PlaceWebSocketService.java
│   └── NotificationWebSocketService.java
│
├── repositories/         # Acces aux donnees
│   ├── PersonneRepository.java
│   ├── PlaceRepository.java
│   ├── ReservationPlaceRepository.java
│   └── VehiculeRepository.java
│
├── models/               # Entites JPA
│   ├── Personne.java
│   ├── Place.java
│   ├── ReservationPlace.java
│   ├── Vehicule.java
│   └── enums/
│
├── dto/                  # Data Transfer Objects
│   ├── PersonneDTO.java
│   ├── PlaceDTO.java
│   ├── ReservationPlaceResponseDTO.java
│   └── ...
│
├── exceptions/           # Exceptions personnalisees
│   ├── AuthenticationException.java
│   ├── BusinessException.java
│   ├── ConflictException.java
│   ├── GlobalExceptionHandler.java
│   ├── OperationNotAllowedException.java
│   ├── ResourceNotFoundException.java
│   └── ValidationException.java
│
├── aspects/              # AOP (Programmation Orientee Aspect)
│   └── LoggingAspect.java
│
├── config/               # Configuration
│   └── WebSocketConfig.java
│
└── security/             # Securite
    ├── JwtUtil.java
    └── SecurityConfig.java
```

---

## Gestion des Exceptions

### Exceptions Personnalisees

| Exception | Code HTTP | Utilisation |
|-----------|-----------|-------------|
| `AuthenticationException` | 401 Unauthorized | Identifiants invalides, token expire |
| `ResourceNotFoundException` | 404 Not Found | Ressource non trouvee |
| `ValidationException` | 400 Bad Request | Donnees invalides |
| `ConflictException` | 409 Conflict | Doublon, conflit de reservation |
| `OperationNotAllowedException` | 400 Bad Request | Operation non permise |
| `BusinessException` | 400 Bad Request | Erreur metier generique |

### GlobalExceptionHandler

Le `GlobalExceptionHandler` (annote `@RestControllerAdvice`) centralise la gestion des erreurs :

- **Pas de try-catch dans les controllers**
- Toutes les exceptions sont capturees automatiquement
- Reponses HTTP standardisees avec format JSON :

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "code": "NOT_FOUND",
  "message": "Place non trouve(e) avec l'id: 123",
  "path": "/api/places/123"
}
```

---

## AOP - Programmation Orientee Aspect

### LoggingAspect

Le `LoggingAspect` fournit un logging automatique pour toute l'application :

- **Controllers** : Log des requetes entrantes et reponses
- **Services** : Log des appels de methodes avec temps d'execution
- **Exceptions** : Log automatique des erreurs

Exemple de logs generes :
```
INFO  - [SERVICE] PlaceService.findAllDTO() - debut
INFO  - [SERVICE] PlaceService.findAllDTO() - fin (45ms)
WARN  - [SERVICE] PlaceService.findByIdDTO() - exception: ResourceNotFoundException
```

---

## Notifications Temps Reel (WebSocket)

### Architecture WebSocket

```
┌─────────────┐     WebSocket      ┌─────────────────────┐
│   Frontend  │ ◄─────────────────► │   Backend Spring    │
│   (React)   │    STOMP/SockJS    │                     │
└─────────────┘                    └─────────────────────┘
```

### Topics disponibles

| Topic | Description |
|-------|-------------|
| `/topic/places` | Mises a jour des places |
| `/topic/places/{id}` | Mise a jour d'une place specifique |
| `/topic/notifications` | Notifications generales |

### Services WebSocket

- **PlaceWebSocketService** : Diffusion des changements de places
- **NotificationWebSocketService** : Notifications utilisateur (creation, modification, suppression)

---

## API REST - Endpoints

### Authentification (`/api/auth`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/login` | Connexion |
| POST | `/api/auth/register` | Inscription |
| GET | `/api/auth/me` | Utilisateur courant |

### Places (`/api/places`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/places` | Liste toutes les places |
| GET | `/api/places/{id}` | Recupere une place |
| GET | `/api/places/disponibles` | Places disponibles |
| GET | `/api/places/availability` | Places avec disponibilite |
| GET | `/api/places/statut/{statut}` | Places par statut |
| GET | `/api/places/type/{type}` | Places par type |
| GET | `/api/places/zone/{zoneId}` | Places par zone |
| POST | `/api/places` | Cree une place |
| PUT | `/api/places/{id}` | Modifie une place |
| PUT | `/api/places/{id}/statut` | Change le statut |
| DELETE | `/api/places/{id}` | Supprime une place |

### Reservations (`/api/reservations-place`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/reservations-place` | Liste les reservations |
| GET | `/api/reservations-place/{id}` | Recupere une reservation |
| GET | `/api/reservations-place/personne/{id}` | Reservations d'une personne |
| GET | `/api/reservations-place/place/{id}` | Reservations d'une place |
| GET | `/api/reservations-place/statut/{statut}` | Reservations par statut |
| POST | `/api/reservations-place` | Cree une reservation |
| PUT | `/api/reservations-place/{id}` | Modifie une reservation |
| POST | `/api/reservations-place/{id}/annuler` | Annule |
| POST | `/api/reservations-place/{id}/commencer` | Demarre |
| POST | `/api/reservations-place/{id}/terminer` | Termine |
| DELETE | `/api/reservations-place/{id}` | Supprime |

### Vehicules (`/api/vehicules`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/vehicules` | Liste les vehicules |
| GET | `/api/vehicules/{id}` | Recupere un vehicule |
| GET | `/api/vehicules/personne/{id}` | Vehicules d'une personne |
| GET | `/api/vehicules/immatriculation/{imm}` | Par immatriculation |
| GET | `/api/vehicules/type/{type}` | Par type |
| POST | `/api/vehicules` | Cree un vehicule |
| PUT | `/api/vehicules/{id}` | Modifie |
| DELETE | `/api/vehicules/{id}` | Supprime |

### Personnes (`/api/personnes`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/personnes` | Liste les personnes |
| GET | `/api/personnes/{id}` | Recupere une personne |
| GET | `/api/personnes/mail/{mail}` | Par email |
| POST | `/api/personnes` | Cree une personne |
| PUT | `/api/personnes/{id}` | Modifie |
| DELETE | `/api/personnes/{id}` | Supprime |

---

## Regles Metier

### Reservations

1. **Validation des dates** : La date de fin doit etre posterieure a la date de debut
2. **Pas de reservation dans le passe** : La date de debut ne peut pas etre dans le passe
3. **Pas de chevauchement** : Impossible de reserver une place deja reservee sur le meme creneau
4. **Limite par personne** : Maximum 3 reservations actives par personne
5. **Place disponible** : La place ne doit pas etre OCCUPEE ou HORS_SERVICE
6. **Vehicule valide** : Le vehicule doit appartenir a la personne qui reserve

### Places

1. **Numero unique** : Chaque place a un numero unique
2. **Position unique** : Pas deux places aux memes coordonnees (x, y)
3. **Suppression securisee** : Impossible de supprimer une place OCCUPEE ou RESERVEE

### Vehicules

1. **Immatriculation unique** : Chaque vehicule a une immatriculation unique
2. **Suppression securisee** : Impossible de supprimer un vehicule avec des reservations actives

---

## Demarrage

### Backend (Spring Boot)

```bash
cd proto-back
./mvnw spring-boot:run
```

Le serveur demarre sur `http://localhost:8080`

### Frontend (React)

```bash
cd proto-front
npm install
npm start
```

L'application demarre sur `http://localhost:3000`

---

## US developpees

### Maryline
US1 :

### Junior

### Tatiana
- WI-01 : Modele de donnees (Parking)
    - Branche : feature/tatiana/WI-01-modele-donnees
