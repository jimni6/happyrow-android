# 🔌 Référence API Backend

Contrat complet de l'API backend. Ce fichier est la **source de vérité** pour les endpoints, les formats de requête/réponse, et les conventions.

---

## Configuration

| Variable | Valeur |
|----------|--------|
| **Base URL** | `https://happyrow-core.onrender.com/event/configuration/api/v1` |
| **Auth** | Header `Authorization: Bearer <supabase_access_token>` |
| **Content-Type** | `application/json` |
| **Convention JSON** | `snake_case` pour tous les champs |

---

## Conventions globales

### Identifiants
- Le backend utilise `identifier` (pas `id`) dans les réponses
- Le frontend/Android utilise `id` dans les domain models
- Le **mapper** fait la conversion `identifier` ↔ `id`

### Timestamps
- Tous les timestamps sont en **millisecondes** (type `Long` en Kotlin)
- Champs : `creation_date`, `update_date`, `event_date`, `joined_at`, `contributed_at`, `created_at`, `updated_at`

### Organisateur
- Le backend utilise `creator` (pas `organizerId`)
- Le mapper convertit `creator` ↔ `organizerId`

### Erreurs HTTP
| Code | Signification | Traitement côté Android |
|------|---------------|------------------------|
| 200 | OK | Parser la réponse |
| 201 | Created | Parser la réponse |
| 400 | Bad Request | Afficher le message d'erreur |
| 401 | Unauthorized | Rediriger vers login (token expiré) |
| 403 | Forbidden | Afficher "Accès refusé" |
| 404 | Not Found | Return `null` dans le repository |
| 500 | Server Error | Afficher "Erreur serveur" |

---

## Events

### POST `/events` — Créer un événement

**Request**
```json
{
  "name": "Anniversaire de Marie",
  "description": "Fête surprise",
  "event_date": "2025-06-15T18:00:00.000Z",
  "location": "Paris",
  "type": "BIRTHDAY"
}
```

**Response** `200`
```json
{
  "identifier": "evt-abc-123",
  "name": "Anniversaire de Marie",
  "description": "Fête surprise",
  "event_date": 1750010400000,
  "location": "Paris",
  "type": "BIRTHDAY",
  "creator": "user-uuid-xxx",
  "creation_date": 1710000000000,
  "update_date": 1710000000000,
  "members": []
}
```

**Note** : `event_date` est envoyé en ISO string dans la requête mais reçu en timestamp (ms) dans la réponse.

### GET `/events` — Liste des événements

**Response** `200` : `List<EventApiResponse>`

**Filtrage** : Le backend retourne tous les événements. Le filtrage par organisateur se fait côté client : `events.filter { it.creator == userId }`.

### GET `/events/{id}` — Détail d'un événement

**Response** `200` : `EventApiResponse`
**Response** `404` : Événement non trouvé

### PUT `/events/{id}` — Mettre à jour un événement

**Request** : même format que POST
**Response** `200` : `EventApiResponse` mis à jour

### DELETE `/events/{id}` — Supprimer un événement

**Response** `200` : Pas de body

---

## Participants

### POST `/events/{eventId}/participants` — Ajouter un participant

**Request**
```json
{
  "user_email": "marie@example.com",
  "status": "INVITED"
}
```

**Response** `200`
```json
{
  "user_email": "marie@example.com",
  "user_name": "Marie Dupont",
  "event_id": "evt-abc-123",
  "status": "INVITED",
  "joined_at": 1710000000000,
  "updated_at": null
}
```

### GET `/events/{eventId}/participants` — Liste des participants

**Response** `200` : `List<ParticipantApiResponse>`

### PUT `/events/{eventId}/participants/{userEmail}` — Mettre à jour le statut

**Request**
```json
{
  "user_email": "marie@example.com",
  "status": "CONFIRMED"
}
```

**Response** `200` : `ParticipantApiResponse` mis à jour

### DELETE `/events/{eventId}/participants/{userEmail}` — Supprimer un participant

**Response** `200` : Pas de body

### Statuts possibles

| Valeur | Description |
|--------|-------------|
| `INVITED` | Invité, pas encore répondu |
| `CONFIRMED` | A confirmé sa présence |
| `MAYBE` | Peut-être présent |
| `DECLINED` | A décliné l'invitation |

---

## Resources

### POST `/events/{eventId}/resources` — Créer une ressource

**Request**
```json
{
  "name": "Gâteau au chocolat",
  "category": "FOOD",
  "quantity": 1,
  "suggested_quantity": 2
}
```

**Response** `200`
```json
{
  "identifier": "res-abc-123",
  "event_id": "evt-abc-123",
  "name": "Gâteau au chocolat",
  "category": "FOOD",
  "current_quantity": 0,
  "suggested_quantity": 2,
  "contributors": [],
  "version": 0,
  "created_at": 1710000000000,
  "updated_at": null
}
```

### GET `/events/{eventId}/resources` — Liste des ressources

**Response** `200` : `List<ResourceApiResponse>`

### GET `/resources/{id}` — Détail d'une ressource

**Note** : Ce endpoint n'est PAS sous `/events/{eventId}/`. Il utilise directement l'ID de la ressource.

**Response** `200` : `ResourceApiResponse`

### PUT `/resources/{id}` — Mettre à jour une ressource

**Note** : Même remarque, endpoint direct sans `eventId`.

**Request** : même format que POST
**Response** `200` : `ResourceApiResponse` mis à jour

### DELETE `/resources/{id}` — Supprimer une ressource

**Response** `200` : Pas de body

### Catégories possibles

| Valeur | Description | Icône suggérée |
|--------|-------------|----------------|
| `FOOD` | Nourriture | 🍔 |
| `DRINK` | Boissons | 🥤 |
| `UTENSIL` | Ustensiles | 🍴 |
| `DECORATION` | Décorations | 🎈 |
| `OTHER` | Autre | 📦 |

### Structure `contributors` dans la réponse

```json
{
  "contributors": [
    {
      "user_id": "user-uuid-xxx",
      "quantity": 1,
      "contributed_at": 1710000000000
    }
  ]
}
```

---

## Contributions

### POST `/events/{eventId}/resources/{resourceId}/contributions` — Créer une contribution

**Request**
```json
{
  "quantity": 2
}
```

**Response** `200`
```json
{
  "identifier": "contrib-abc-123",
  "participant_id": "user-uuid-xxx",
  "resource_id": "res-abc-123",
  "quantity": 2,
  "created_at": 1710000000000,
  "updated_at": null
}
```

**Note** : Le backend identifie le contributeur via le token Bearer (pas besoin d'envoyer le `userId`).

### GET `/events/{eventId}/resources/{resourceId}/contributions` — Liste des contributions

**Response** `200` : `List<ContributionApiResponse>`

### DELETE `/events/{eventId}/resources/{resourceId}/contributions` — Supprimer sa contribution

**Note** : Le backend identifie la contribution à supprimer via le token Bearer.

**Response** `200` : Pas de body

---

## Mapping DTO → Domain — Résumé

| Champ backend (snake_case) | Champ domain (camelCase) | Notes |
|---------------------------|-------------------------|-------|
| `identifier` | `id` | Tous les modèles |
| `creator` | `organizerId` | Event uniquement |
| `event_date` | `date` | Event (timestamp ms) |
| `creation_date` | `createdAt` | Event |
| `update_date` | `updatedAt` | Event |
| `user_email` | `userEmail` | Participant |
| `user_name` | `userName` | Participant |
| `event_id` | `eventId` | Participant, Resource |
| `joined_at` | `joinedAt` | Participant |
| `updated_at` | `updatedAt` | Participant, Resource, Contribution |
| `current_quantity` | `currentQuantity` | Resource |
| `suggested_quantity` | `suggestedQuantity` | Resource |
| `created_at` | `createdAt` | Resource, Contribution |
| `contributed_at` | `contributedAt` | ResourceContributor |
| `user_id` | `userId` | ResourceContributor |
| `participant_id` | `userId` | Contribution (renommé!) |
| `resource_id` | `resourceId` | Contribution |

---

## Types d'événements

| Valeur API | Enum Kotlin |
|-----------|-------------|
| `"PARTY"` | `EventType.PARTY` |
| `"BIRTHDAY"` | `EventType.BIRTHDAY` |
| `"DINER"` | `EventType.DINER` |
| `"SNACK"` | `EventType.SNACK` |

---

## Authentification (Supabase)

L'authentification n'utilise **pas** l'API backend mais le SDK Supabase directement.

| Opération | SDK Kotlin |
|-----------|-----------|
| Register | `supabase.auth.signUpWith(Email) { email; password; data = mapOf("firstname" to ...) }` |
| Sign in | `supabase.auth.signInWith(Email) { email; password }` |
| Sign out | `supabase.auth.signOut()` |
| Current user | `supabase.auth.currentUserOrNull()` |
| Current session | `supabase.auth.currentSessionOrNull()` |
| Refresh | `supabase.auth.refreshCurrentSession()` |
| Reset password | `supabase.auth.resetPasswordForEmail(email)` |
| Update password | `supabase.auth.updateUser { password = newPassword }` |
| Google OAuth | `supabase.auth.signInWith(Google)` |
| Auth state | `supabase.auth.sessionStatus` (Flow) |

### Mapping User Supabase → Domain User

| Champ Supabase | Champ Domain |
|---------------|-------------|
| `user.id` | `id` |
| `user.email` | `email` |
| `user.emailConfirmedAt != null` | `emailConfirmed` |
| `user.userMetadata["firstname"]` | `firstname` |
| `user.userMetadata["lastname"]` | `lastname` |
| `user.createdAt` | `createdAt` |
| `user.updatedAt` | `updatedAt` |
