# 🏗️ Architecture — Patterns et décisions

Ce fichier documente les patterns d'architecture à suivre rigoureusement. Toute déviation introduit de l'incohérence.

---

## Clean Architecture — Vue d'ensemble

```
┌─────────────────────────────────────────┐
│                  UI                      │  Compose, ViewModels
│         (dépend de UseCases)             │  Peut importer Android/Compose
├─────────────────────────────────────────┤
│              Use Cases                   │  Logique métier + validations
│         (dépend de Domain)               │  Kotlin pur, pas d'Android
├─────────────────────────────────────────┤
│               Domain                     │  Models + Interfaces
│         (ne dépend de rien)              │  Kotlin pur, pas d'Android
├─────────────────────────────────────────┤
│                Data                      │  Retrofit, DTOs, Mappers, Implémentations
│         (dépend de Domain)               │  Peut importer libs réseau
├─────────────────────────────────────────┤
│                 DI                       │  Modules Hilt
│         (assemble tout)                  │  Connaît toutes les couches
└─────────────────────────────────────────┘
```

### Règle de dépendance

Les dépendances vont **toujours vers l'intérieur** (vers Domain) :

- `UI` → `UseCases` → `Domain` ← `Data`
- `UI` ne connaît **jamais** `Data` directement
- `Domain` ne connaît **personne**
- `Data` implémente les interfaces de `Domain`
- `DI` (Hilt modules) est le seul à connaître tout le monde

---

## Flux de données typique

```
User tap → Composable → ViewModel → UseCase → Repository(interface) → RepositoryImpl → Retrofit → API
                                                                                              ↓
User sees ← Composable ← ViewModel ← UseCase ← Repository(interface) ← RepositoryImpl ← Response
```

### Exemple concret : Créer un événement

```
1. User remplit le formulaire et tape "Créer"
2. CreateEventScreen appelle viewModel.createEvent(request)
3. EventsViewModel.createEvent() lance une coroutine :
   a. _createState.value = CreateEventUiState(isLoading = true)
   b. val event = createEventUseCase(request)    // ← Use Case
   c. _createState.value = CreateEventUiState(success = true)
4. CreateEvent.invoke(request) :
   a. require(name >= 3)...                       // ← Validation
   b. return eventRepository.createEvent(request) // ← Interface
5. EventRepositoryImpl.createEvent(request) :
   a. val apiRequest = request.toApiRequest()     // ← Mapper Domain→DTO
   b. val response = eventApi.createEvent(apiRequest) // ← Retrofit
   c. return response.toDomain()                  // ← Mapper DTO→Domain
6. Retrofit envoie POST /events avec Bearer token (via AuthInterceptor)
7. La réponse remonte : API → DTO → Domain → ViewModel → UI
```

---

## Injection de dépendances (Hilt)

### Modules

| Module | Fichier | Scope | Fournit |
|--------|---------|-------|---------|
| `AuthModule` | `di/AuthModule.kt` | `SingletonComponent` | `SupabaseClient`, `AuthRepository` |
| `NetworkModule` | `di/NetworkModule.kt` | `SingletonComponent` | `OkHttpClient`, `Retrofit`, `*Api` interfaces |
| `RepositoryModule` | `di/RepositoryModule.kt` | `SingletonComponent` | `*Repository` bindings |

### Annotations par couche

| Couche | Annotation |
|--------|-----------|
| `@HiltAndroidApp` | `HappyRowApplication` |
| `@AndroidEntryPoint` | `MainActivity` |
| `@HiltViewModel` | Tous les ViewModels |
| `@Inject constructor` | Use cases, Repository impls, TokenProvider |
| `@Module @InstallIn(SingletonComponent::class)` | Tous les modules DI |
| `@Provides @Singleton` | Pour les instances (Retrofit, OkHttp, Supabase) |
| `@Binds @Singleton` | Pour les bindings interface → impl |

### Graphe de dépendances

```
AuthModule
├── SupabaseClient
└── AuthRepository (→ SupabaseAuthRepository)
    └── SupabaseClient

NetworkModule
├── TokenProvider
│   └── SupabaseClient (from AuthModule)
├── AuthInterceptor
│   └── TokenProvider
├── OkHttpClient
│   └── AuthInterceptor
├── Retrofit
│   └── OkHttpClient
├── EventApi ← Retrofit
├── ParticipantApi ← Retrofit
├── ResourceApi ← Retrofit
└── ContributionApi ← Retrofit

RepositoryModule
├── EventRepository → EventRepositoryImpl(EventApi)
├── ParticipantRepository → ParticipantRepositoryImpl(ParticipantApi)
├── ResourceRepository → ResourceRepositoryImpl(ResourceApi)
└── ContributionRepository → ContributionRepositoryImpl(ContributionApi)

ViewModels (auto-injectés par Hilt)
├── AuthViewModel(RegisterUser, SignInUser, SignOutUser, GetCurrentUser, ResetPassword, SignInWithProvider, AuthRepository)
├── EventsViewModel(CreateEvent, GetEventsByOrganizer, GetEventById, UpdateEvent, DeleteEvent)
├── ParticipantsViewModel(AddParticipant, GetParticipants, UpdateParticipantStatus, RemoveParticipant)
├── ResourcesViewModel(CreateResource, GetResources, UpdateResource, DeleteResource)
└── ContributionsViewModel(AddContribution, GetContributions, UpdateContribution, DeleteContribution)
```

---

## Navigation

### Architecture

```
MainActivity
└── HappyRowTheme
    └── HappyRowNavHost(navController, authState)
        ├── Auth flow (si Unauthenticated)
        │   ├── WelcomeScreen
        │   ├── LoginScreen
        │   ├── RegisterScreen
        │   └── ForgotPasswordScreen
        └── Main flow (si Authenticated)
            ├── HomeScreen (liste événements)
            ├── EventDetailScreen(eventId)
            └── ProfileScreen
```

### Principes de navigation

1. **Le NavHost décide** — Le composable `HappyRowNavHost` contient toutes les routes
2. **Les écrans reçoivent des callbacks** — Jamais de `navController` passé à un écran
3. **AuthState drive la destination de départ** — `Authenticated` → Home, sinon → Welcome
4. **Arguments via route** — `events/{eventId}` avec `navArgument`

```kotlin
// ✅ Bon : callback de navigation
@Composable
fun HomeScreen(onNavigateToEvent: (String) -> Unit) { ... }

// ❌ Mauvais : navController dans l'écran
@Composable
fun HomeScreen(navController: NavController) { ... }
```

---

## State management

### Principes

1. **Single source of truth** — Le ViewModel détient l'état, l'UI le lit
2. **Unidirectional data flow** — UI → ViewModel (actions) → UI (state)
3. **StateFlow** — Toujours `StateFlow`, jamais `LiveData`
4. **Immutable state** — Les sealed class et data class sont immutables

```
┌──────────┐  action()   ┌────────────┐  invoke()  ┌──────────┐
│    UI    │ ──────────→ │  ViewModel │ ─────────→ │ Use Case │
│ Compose  │             │  StateFlow │            │ Suspend  │
│          │ ←────────── │            │ ←───────── │          │
└──────────┘  state      └────────────┘  result    └──────────┘
```

### Quand créer un ViewModel séparé vs intégrer

| Situation | Décision |
|-----------|----------|
| Écran avec sa propre liste de données | ViewModel séparé |
| Section dans un écran existant (ex: participants dans EventDetail) | ViewModel séparé, injecté dans le composable de la section |
| Dialog simple (ex: ContributeDialog) | Utiliser le ViewModel parent |

---

## Gestion du token d'authentification

```
SupabaseClient
    ↓ (session.accessToken)
TokenProvider
    ↓ (getToken())
AuthInterceptor (OkHttp)
    ↓ (Authorization: Bearer xxx)
Retrofit → API Backend
```

Le token est **automatiquement injecté** dans toutes les requêtes Retrofit via l'intercepteur OkHttp. Aucun composant UI ou use case n'a besoin de manipuler le token directement.

---

## Décisions d'architecture

| Décision | Choix | Justification |
|----------|-------|---------------|
| State management | `StateFlow` | Mieux intégré avec Coroutines que LiveData |
| DI | Hilt | Standard Android, moins de boilerplate que Dagger pur |
| Navigation | Compose Navigation | Intégré avec Compose, supporte les arguments typés |
| Sérialisation | Kotlinx Serialization | Multiplateforme, performant, pas de réflexion |
| HTTP | Retrofit + OkHttp | Standard Android, intercepteurs, logging |
| Auth | Supabase Kotlin SDK | Même backend que la web app, SDK officiel |
| Tests | JUnit 5 + MockK | MockK est plus idiomatique Kotlin que Mockito |
| Use case pattern | `operator fun invoke` | Permet l'appel direct `useCase(params)` |
| Error handling | Exceptions | Plus simple que Result/Either pour ce projet |
