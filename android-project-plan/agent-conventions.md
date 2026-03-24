# 📐 Conventions de code — Kotlin / Android

Respecte ces conventions dans **chaque fichier** que tu crées. La cohérence est plus importante que la perfection.

---

## Nommage

### Fichiers et classes

| Élément | Convention | Exemple |
|---------|-----------|---------|
| Package | `lowercase` | `com.happyrow.android.domain.model` |
| Classe / Object | `PascalCase` | `EventRepositoryImpl` |
| Interface | `PascalCase` (pas de préfixe `I`) | `EventRepository` |
| Data class | `PascalCase` | `EventCreationRequest` |
| Enum class | `PascalCase` | `EventType` |
| Enum value | `SCREAMING_SNAKE_CASE` | `BIRTHDAY`, `CONFIRMED` |
| Sealed class | `PascalCase` | `AuthState` |
| Composable | `PascalCase` | `EventCard()` |
| ViewModel | `PascalCase` + suffixe `ViewModel` | `EventsViewModel` |
| Use case | `PascalCase` (verbe + nom) | `CreateEvent`, `GetParticipants` |
| DTO | `PascalCase` + suffixe `ApiRequest` / `ApiResponse` | `EventApiRequest` |

### Variables et fonctions

| Élément | Convention | Exemple |
|---------|-----------|---------|
| Variable | `camelCase` | `eventName`, `isLoading` |
| Fonction | `camelCase` | `loadEvents()`, `signIn()` |
| Constante | `SCREAMING_SNAKE_CASE` dans `companion object` | `const val MIN_PASSWORD_LENGTH = 8` |
| StateFlow privé | `_camelCase` avec underscore préfixe | `private val _authState` |
| StateFlow public | `camelCase` sans underscore | `val authState: StateFlow<AuthState>` |

### JSON / Backend

| Contexte | Convention |
|----------|-----------|
| Champs DTO (backend) | `snake_case` via `@Serializable` + `@SerialName` si nécessaire |
| Champs domain (Kotlin) | `camelCase` |
| Le mapper fait la conversion | `snake_case` ↔ `camelCase` |

---

## Structure d'un fichier Kotlin

```kotlin
// 1. Package
package com.happyrow.android.domain.model

// 2. Imports (groupés : android, androidx, com.happyrow, libs tierces, java/kotlin)
import ...

// 3. Classe/interface/function principale
// 4. Classes secondaires liées (si petites)
```

**Règle** : 1 classe publique principale par fichier. Les sealed class children et petites classes liées peuvent cohabiter.

---

## Patterns récurrents

### Use Case

```kotlin
class CreateEvent @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(request: EventCreationRequest): Event {
        // 1. Validations avec require()
        require(request.name.length >= 3) { "Le nom doit contenir au moins 3 caractères" }
        require(request.location.length >= 3) { "Le lieu doit contenir au moins 3 caractères" }
        require(request.organizerId.isNotBlank()) { "L'organisateur est requis" }

        // 2. Appel repository
        return eventRepository.createEvent(request)
    }
}
```

**Règles** :
- `@Inject constructor` pour Hilt
- `operator fun invoke` (appel direct : `createEvent(request)`)
- `suspend` car les repositories sont suspend
- Validations avec `require()` → throw `IllegalArgumentException`
- **Aucun import Android** — Kotlin pur

### ViewModel

```kotlin
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val createEvent: CreateEvent,
    private val getEventsByOrganizer: GetEventsByOrganizer
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    fun loadEvents(organizerId: String) {
        viewModelScope.launch {
            _uiState.value = EventsUiState.Loading
            try {
                val events = getEventsByOrganizer(organizerId)
                _uiState.value = EventsUiState.Success(events)
            } catch (e: Exception) {
                _uiState.value = EventsUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}
```

**Règles** :
- `@HiltViewModel` + `@Inject constructor`
- State = `StateFlow` (**pas** `LiveData`)
- Mutations dans `viewModelScope.launch`
- `try/catch` systématique autour des appels use case
- Injecter les **use cases**, pas les repositories

### Repository Implementation

```kotlin
class EventRepositoryImpl @Inject constructor(
    private val eventApi: EventApi
) : EventRepository {

    override suspend fun createEvent(eventData: EventCreationRequest): Event {
        val response = eventApi.createEvent(eventData.toApiRequest())
        return response.toDomain()
    }

    override suspend fun getEventById(id: String): Event? {
        return try {
            eventApi.getEventById(id).toDomain()
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
    }
}
```

**Règles** :
- Extension functions du mapper (`toApiRequest()`, `toDomain()`)
- HTTP 404 → `null`
- Autres erreurs HTTP → propagées

### Mapper (extension functions)

```kotlin
// Fichier dédié : data/remote/mapper/EventMapper.kt
fun EventApiResponse.toDomain(): Event { ... }
fun EventCreationRequest.toApiRequest(): EventApiRequest { ... }
```

### Composable Screen

```kotlin
@Composable
fun HomeScreen(
    viewModel: EventsViewModel = hiltViewModel(),
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(...) },
        floatingActionButton = { ... }
    ) { padding ->
        when (uiState) {
            is EventsUiState.Loading -> LoadingScreen()
            is EventsUiState.Error -> ErrorMessage(...)
            is EventsUiState.Success -> { LazyColumn(...) { ... } }
        }
    }
}
```

**Règles** :
- `hiltViewModel()` pour l'injection
- `collectAsStateWithLifecycle()` pour observer les StateFlow
- Callbacks de navigation en paramètre (**pas** de `navController` dans le composable)
- `when` exhaustif sur les sealed class
- `Scaffold` pour la structure de page

---

## Sealed class d'état UI

```kotlin
// Pour un écran qui charge une liste
sealed class EventsUiState {
    object Loading : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val message: String) : EventsUiState()
}

// Pour une action (create, update, delete)
data class CreateEventUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
```

---

## Gestion des erreurs par couche

| Couche | Stratégie |
|--------|-----------|
| **Domain / Use Case** | `require()` → throw `IllegalArgumentException` |
| **Data / Repository** | HTTP 404 → `null`. Autres HTTP → remonter `HttpException`. Réseau → remonter `IOException` |
| **UI / ViewModel** | `try/catch(Exception)` dans `viewModelScope.launch` → mapper en `UiState.Error(message)` |

---

## Compose spécifique

- **Modifier** : toujours `modifier: Modifier = Modifier` comme premier param optionnel
- **Preview** : `@Preview` sur les composants isolés (cartes, boutons), pas sur les écrans entiers
- **Strings** : dans `res/values/strings.xml` pour l'UI. Exceptions : messages de validation dans les use cases

---

## Imports — Règle absolue

**Jamais** d'import `android.*` ou `androidx.*` dans :
- `domain/model/`
- `domain/repository/`
- `usecases/`

Le domain layer est du **Kotlin pur**, sans aucune dépendance Android.
