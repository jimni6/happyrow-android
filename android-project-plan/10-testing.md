# Phase 9 — Testing

Stratégie et tâches de tests pour l'application Android. Couverture à 3 niveaux : unitaire, intégration, et UI.

> **Source web** : `happyrow-front/src/__tests__/`

---

## Stack de tests

| Type | Outil | Cible |
|------|-------|-------|
| **Unitaire** | JUnit 5 + MockK | Use cases, ViewModels, Mappers |
| **Intégration** | JUnit 5 + MockK + Coroutines Test | Repository implementations, Hilt modules |
| **UI** | Espresso + Compose Testing | Écrans, navigation, interactions utilisateur |

---

## Tâches

### T-9.1 : Configuration des tests

- [ ] Vérifier que les dépendances test sont en place (voir `01-project-setup.md`)
- [ ] Configurer `kotlinx-coroutines-test` pour les tests avec `runTest`
- [ ] Créer `TestDispatcherRule` pour remplacer les dispatchers dans les tests

```kotlin
class TestDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

### T-9.2 : Test utilities et fakes

- [ ] Créer `FakeAuthRepository` — implémentation in-memory de `AuthRepository`
- [ ] Créer `FakeEventRepository` — implémentation in-memory de `EventRepository`
- [ ] Créer `FakeParticipantRepository` — implémentation in-memory de `ParticipantRepository`
- [ ] Créer `FakeResourceRepository` — implémentation in-memory de `ResourceRepository`
- [ ] Créer `FakeContributionRepository` — implémentation in-memory de `ContributionRepository`
- [ ] Créer des factory functions pour les objets de test :

```kotlin
object TestData {
    fun createUser(
        id: String = "user-123",
        email: String = "test@example.com",
        firstname: String = "John",
        lastname: String = "Doe"
    ) = User(id, email, true, firstname, lastname, System.currentTimeMillis(), System.currentTimeMillis())

    fun createEvent(
        id: String = "event-123",
        name: String = "Test Event",
        organizerId: String = "user-123"
    ) = Event(id, name, "Description", System.currentTimeMillis() + 86400000, "Paris", EventType.PARTY, organizerId)

    fun createParticipant(
        userEmail: String = "participant@example.com",
        eventId: String = "event-123",
        status: ParticipantStatus = ParticipantStatus.INVITED
    ) = Participant(userEmail, null, eventId, status, System.currentTimeMillis())

    // ... idem pour Resource, Contribution
}
```

> **Ref web** : `src/__tests__/utils/testUtils.tsx` — MockAuthRepository et factories

### T-9.3 : Tests unitaires — Use Cases Auth

- [ ] Tester `RegisterUser` :
  - ✅ Register avec données valides
  - ❌ Email vide → erreur
  - ❌ Password < 8 chars → erreur
  - ❌ Firstname < 2 chars → erreur
  - ❌ Lastname < 2 chars → erreur
  - ❌ Passwords ne correspondent pas → erreur
  - ❌ Email format invalide → erreur
  - ❌ Repository throw → erreur propagée

- [ ] Tester `SignInUser` :
  - ✅ Sign in avec credentials valides
  - ❌ Email vide → erreur
  - ❌ Password vide → erreur
  - ❌ Repository throw → erreur propagée

- [ ] Tester `SignOutUser`
- [ ] Tester `GetCurrentUser`
- [ ] Tester `ResetPassword`
- [ ] Tester `SignInWithProvider`

> **Ref web** : `src/__tests__/infrastructure/SupabaseAuthRepository.test.ts` — 18 tests

### T-9.4 : Tests unitaires — Use Cases Events

- [ ] Tester `CreateEvent` :
  - ✅ Création avec données valides
  - ❌ Nom < 3 chars → erreur
  - ❌ Location < 3 chars → erreur
  - ❌ Date dans le passé → erreur
  - ❌ OrganizerId vide → erreur

- [ ] Tester `GetEventsByOrganizer`
- [ ] Tester `GetEventById`
- [ ] Tester `UpdateEvent`
- [ ] Tester `DeleteEvent`

### T-9.5 : Tests unitaires — Use Cases Participants, Resources, Contributions

- [ ] Tester `AddParticipant` (validation email, eventId)
- [ ] Tester `GetParticipants`
- [ ] Tester `UpdateParticipantStatus`
- [ ] Tester `RemoveParticipant`
- [ ] Tester `CreateResource` (validation name, category, quantity)
- [ ] Tester `GetResources`
- [ ] Tester `UpdateResource`
- [ ] Tester `DeleteResource`
- [ ] Tester `AddContribution` (validation quantity > 0)
- [ ] Tester `GetContributions`
- [ ] Tester `UpdateContribution`
- [ ] Tester `DeleteContribution`

### T-9.6 : Tests unitaires — Mappers

- [ ] Tester `EventMapper` (DTO → Domain et Domain → DTO)
  - Vérifier le mapping `identifier` → `id`
  - Vérifier le mapping `creator` → `organizerId`
  - Vérifier le mapping `event_date` (timestamp) → `date`
- [ ] Tester `ParticipantMapper`
- [ ] Tester `ResourceMapper`
  - Vérifier le mapping des `contributors`
- [ ] Tester `ContributionMapper`

### T-9.7 : Tests unitaires — ViewModels

- [ ] Tester `AuthViewModel` :
  - État initial = Loading
  - Login réussi → Authenticated
  - Login échoué → Error dans loginState
  - Register réussi → registrationSuccess = true
  - Sign out → Unauthenticated

- [ ] Tester `EventsViewModel` :
  - loadEvents → Success avec liste
  - loadEvents vide → Success avec liste vide
  - createEvent → Success + refresh liste
  - deleteEvent → refresh liste
  - Erreur réseau → Error state

- [ ] Tester `ParticipantsViewModel`
- [ ] Tester `ResourcesViewModel`
- [ ] Tester `ContributionsViewModel`

### T-9.8 : Tests UI — Écrans Auth (Espresso + Compose Testing)

- [ ] Tester `WelcomeScreen` :
  - Affiche les boutons "Créer un compte" et "Se connecter"
  - Click sur "Créer un compte" → navigation

- [ ] Tester `LoginScreen` :
  - Affiche les champs email/password
  - Submit avec champs vides → erreur visible
  - Submit valide → appel au ViewModel

- [ ] Tester `RegisterScreen` :
  - Affiche tous les champs (firstname, lastname, email, password, confirmPassword)
  - Validation en temps réel
  - Submit valide → appel au ViewModel

> **Ref web** :
> - `src/__tests__/presentation/screens/AuthScreen.test.tsx` — 12 tests
> - `src/__tests__/presentation/components/ForgotPasswordForm.test.tsx` — 15 tests

### T-9.9 : Tests UI — Écrans principaux

- [ ] Tester `HomeScreen` :
  - Affiche la salutation avec le prénom
  - Affiche la liste des événements
  - FAB visible pour créer un événement

- [ ] Tester `EventDetailScreen` :
  - Affiche les détails de l'événement
  - Affiche les sections Participants et Resources

- [ ] Tester la navigation globale :
  - Non authentifié → WelcomeScreen
  - Authentifié → HomeScreen
  - Click sur event → EventDetailScreen
  - Back → retour correct

### T-9.10 : Tests d'intégration — Repository avec API mock

- [ ] Utiliser MockWebServer (OkHttp) pour simuler les réponses API
- [ ] Tester `EventRepositoryImpl` avec des réponses JSON réelles
- [ ] Tester la gestion des erreurs HTTP (400, 401, 404, 500)
- [ ] Tester l'injection du token Bearer

---

## Objectifs de couverture

| Couche | Couverture cible |
|--------|-----------------|
| Use Cases | ≥ 90% |
| ViewModels | ≥ 80% |
| Mappers | 100% |
| Repository impls | ≥ 70% |
| Écrans UI | ≥ 60% (happy paths + validations) |

---

## Critères de validation

- Tous les tests passent en CI
- Les Fake repositories sont réutilisables pour tout nouveau test
- Les tests ViewModel utilisent `TestDispatcherRule` systématiquement
- Les tests UI couvrent les parcours critiques (auth, création event, contribution)
- Aucun test ne dépend du réseau réel
