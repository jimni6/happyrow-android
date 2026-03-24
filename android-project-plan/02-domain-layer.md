# Phase 1 — Domain Layer

Tous les modèles et interfaces repository. Cette couche n'a **aucune dépendance Android** — uniquement du Kotlin pur.

> **Source web** : `happyrow-front/src/features/*/types/`

---

## Tâches

### T-1.1 : Modèle `User` et types auth

- [ ] Créer `domain/model/User.kt`

```kotlin
data class User(
    val id: String,
    val email: String,
    val emailConfirmed: Boolean,
    val firstname: String,
    val lastname: String,
    val createdAt: Long,    // timestamp ms
    val updatedAt: Long,
    val metadata: Map<String, Any?>? = null
)
```

- [ ] Créer `domain/model/UserCredentials.kt`

```kotlin
data class UserCredentials(
    val email: String,
    val password: String
)
```

- [ ] Créer `domain/model/UserRegistration.kt`

```kotlin
data class UserRegistration(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val confirmPassword: String? = null,
    val metadata: Map<String, Any?>? = null
)
```

- [ ] Créer `domain/model/AuthSession.kt`

```kotlin
data class AuthSession(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long   // timestamp ms
)
```

> **Ref web** : `src/features/auth/types/User.ts`

### T-1.2 : Modèle `Event`

- [ ] Créer `domain/model/Event.kt`

```kotlin
enum class EventType {
    PARTY, BIRTHDAY, DINER, SNACK
}

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val date: Long,          // timestamp ms
    val location: String,
    val type: EventType,
    val organizerId: String
)

data class EventCreationRequest(
    val name: String,
    val description: String,
    val date: String,        // ISO string pour l'API
    val location: String,
    val type: EventType,
    val organizerId: String
)
```

> **Ref web** : `src/features/events/types/Event.ts`

### T-1.3 : Modèle `Participant`

- [ ] Créer `domain/model/Participant.kt`

```kotlin
enum class ParticipantStatus {
    INVITED, CONFIRMED, MAYBE, DECLINED
}

data class Participant(
    val userEmail: String,
    val userName: String? = null,
    val eventId: String,
    val status: ParticipantStatus,
    val joinedAt: Long,
    val updatedAt: Long? = null
)

data class ParticipantCreationRequest(
    val eventId: String,
    val userEmail: String,
    val status: ParticipantStatus
)

data class ParticipantUpdateRequest(
    val status: ParticipantStatus
)
```

> **Ref web** : `src/features/participants/types/Participant.ts`

### T-1.4 : Modèle `Resource`

- [ ] Créer `domain/model/Resource.kt`

```kotlin
enum class ResourceCategory {
    FOOD, DRINK, UTENSIL, DECORATION, OTHER
}

data class ResourceContributor(
    val userId: String,
    val quantity: Int,
    val contributedAt: Long
)

data class Resource(
    val id: String,
    val eventId: String,
    val name: String,
    val category: ResourceCategory,
    val currentQuantity: Int,
    val suggestedQuantity: Int? = null,
    val contributors: List<ResourceContributor>,
    val createdAt: Long,
    val updatedAt: Long? = null
)

data class ResourceCreationRequest(
    val eventId: String,
    val name: String,
    val category: ResourceCategory,
    val quantity: Int,
    val suggestedQuantity: Int? = null
)

data class ResourceUpdateRequest(
    val name: String? = null,
    val category: ResourceCategory? = null,
    val quantity: Int? = null,
    val suggestedQuantity: Int? = null
)
```

> **Ref web** : `src/features/resources/types/Resource.ts`

### T-1.5 : Modèle `Contribution`

- [ ] Créer `domain/model/Contribution.kt`

```kotlin
data class Contribution(
    val id: String,
    val eventId: String,
    val resourceId: String,
    val userId: String,
    val quantity: Int,
    val createdAt: Long
)

data class ContributionCreationRequest(
    val eventId: String,
    val resourceId: String,
    val userId: String,
    val quantity: Int
)

data class ContributionUpdateRequest(
    val quantity: Int? = null
)
```

> **Ref web** : `src/features/contributions/types/Contribution.ts`

### T-1.6 : Interface `AuthRepository`

- [ ] Créer `domain/repository/AuthRepository.kt`

```kotlin
interface AuthRepository {
    suspend fun register(userData: UserRegistration): User
    suspend fun signIn(credentials: UserCredentials): AuthSession
    suspend fun signOut()
    suspend fun getCurrentUser(): User?
    suspend fun getCurrentSession(): AuthSession?
    suspend fun refreshSession(): AuthSession
    suspend fun resetPassword(email: String)
    suspend fun updatePassword(newPassword: String)
    suspend fun signInWithProvider(provider: String)
    fun onAuthStateChange(callback: (AuthSession?) -> Unit): () -> Unit
}
```

> **Ref web** : `src/features/auth/types/AuthRepository.ts`

### T-1.7 : Interface `EventRepository`

- [ ] Créer `domain/repository/EventRepository.kt`

```kotlin
interface EventRepository {
    suspend fun createEvent(eventData: EventCreationRequest): Event
    suspend fun getEventById(id: String): Event?
    suspend fun getEventsByOrganizer(organizerId: String): List<Event>
    suspend fun updateEvent(id: String, eventData: EventCreationRequest): Event
    suspend fun deleteEvent(id: String, userId: String)
}
```

> **Ref web** : `src/features/events/types/EventRepository.ts`

### T-1.8 : Interface `ParticipantRepository`

- [ ] Créer `domain/repository/ParticipantRepository.kt`

```kotlin
interface ParticipantRepository {
    suspend fun addParticipant(data: ParticipantCreationRequest): Participant
    suspend fun getParticipantsByEvent(eventId: String): List<Participant>
    suspend fun updateParticipantStatus(
        eventId: String,
        userEmail: String,
        data: ParticipantUpdateRequest
    ): Participant
    suspend fun removeParticipant(eventId: String, userEmail: String)
}
```

> **Ref web** : `src/features/participants/types/ParticipantRepository.ts`

### T-1.9 : Interface `ResourceRepository`

- [ ] Créer `domain/repository/ResourceRepository.kt`

```kotlin
interface ResourceRepository {
    suspend fun createResource(data: ResourceCreationRequest): Resource
    suspend fun getResourcesByEvent(eventId: String): List<Resource>
    suspend fun getResourceById(id: String): Resource?
    suspend fun updateResource(id: String, data: ResourceUpdateRequest): Resource
    suspend fun deleteResource(id: String)
}
```

> **Ref web** : `src/features/resources/types/ResourceRepository.ts`

### T-1.10 : Interface `ContributionRepository`

- [ ] Créer `domain/repository/ContributionRepository.kt`

```kotlin
interface ContributionRepository {
    suspend fun getContributionsByResource(eventId: String, resourceId: String): List<Contribution>
    suspend fun createContribution(data: ContributionCreationRequest): Contribution
    suspend fun updateContribution(eventId: String, resourceId: String, data: ContributionUpdateRequest): Contribution
    suspend fun deleteContribution(eventId: String, resourceId: String)
}
```

> **Ref web** : `src/features/contributions/types/ContributionRepository.ts`

---

## Critères de validation

- Tous les fichiers compilent sans erreur
- Aucune dépendance Android dans `domain/`
- Les enums correspondent exactement aux valeurs du backend (PARTY, BIRTHDAY, etc.)
- Les interfaces repository couvrent toutes les opérations de la web app
