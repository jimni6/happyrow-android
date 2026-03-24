# Phase 5 — Feature Events

Écrans et logique pour la gestion des événements : liste, création, détail, édition, suppression.

> **Source web** : `happyrow-front/src/features/events/`

---

## Tâches

### T-5.1 : Use Cases Events

- [ ] Créer `usecases/events/CreateEvent.kt`
  - Validation : name ≥ 3 chars, description non vide, location ≥ 3 chars, type valide, date dans le futur, organizerId non vide
  - Appeler `eventRepository.createEvent()`

- [ ] Créer `usecases/events/GetEventsByOrganizer.kt`
  - Appeler `eventRepository.getEventsByOrganizer(organizerId)`

- [ ] Créer `usecases/events/GetEventById.kt`
  - Validation : id non vide
  - Appeler `eventRepository.getEventById(id)`

- [ ] Créer `usecases/events/UpdateEvent.kt`
  - Mêmes validations que CreateEvent
  - Appeler `eventRepository.updateEvent(id, data)`

- [ ] Créer `usecases/events/DeleteEvent.kt`
  - Validation : id et userId non vides
  - Appeler `eventRepository.deleteEvent(id, userId)`

> **Ref web** : `src/features/events/use-cases/` — 5 use cases

### T-5.2 : EventsViewModel

- [ ] Créer `ui/events/EventsViewModel.kt`

```kotlin
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val createEvent: CreateEvent,
    private val getEventsByOrganizer: GetEventsByOrganizer,
    private val getEventById: GetEventById,
    private val updateEvent: UpdateEvent,
    private val deleteEvent: DeleteEvent
) : ViewModel() {

    private val _eventsState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val eventsState: StateFlow<EventsUiState> = _eventsState.asStateFlow()

    private val _createState = MutableStateFlow(CreateEventUiState())
    val createState: StateFlow<CreateEventUiState> = _createState.asStateFlow()

    fun loadEvents(organizerId: String) { /* ... */ }
    fun createEvent(request: EventCreationRequest) { /* ... */ }
    fun deleteEvent(eventId: String, userId: String) { /* ... */ }
}

sealed class EventsUiState {
    object Loading : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val message: String) : EventsUiState()
}

data class CreateEventUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
```

> **Ref web** : `src/features/events/hooks/useEvents.ts` et `src/features/events/hooks/EventsProvider.tsx`

### T-5.3 : EventDetailViewModel

- [ ] Créer `ui/events/EventDetailViewModel.kt`
- [ ] Charger l'événement par ID
- [ ] Gérer l'état de chargement, erreur, et données
- [ ] Exposer les actions : update, delete, manage participants/resources

### T-5.4 : Écran — HomeScreen (liste événements)

- [ ] Créer `ui/home/HomeScreen.kt`
- [ ] Salutation personnalisée basée sur l'heure (Bonjour/Bon après-midi/Bonsoir)
- [ ] Avatar utilisateur avec initiales
- [ ] Statut de vérification email
- [ ] Liste des événements de l'utilisateur (`LazyColumn`)
- [ ] Bouton FAB "Créer un événement"
- [ ] Pull-to-refresh pour recharger les événements
- [ ] État vide quand pas d'événements

> **Ref web** : `src/features/home/HomeScreen.tsx` — dashboard complet

### T-5.5 : Composant — `EventCard`

- [ ] Créer `ui/events/components/EventCard.kt`
- [ ] Afficher : nom, date formatée, lieu, type (avec icône/badge), nombre de participants
- [ ] Click → navigation vers EventDetail
- [ ] Design Material 3 Card

> **Ref web** : `src/features/events/components/EventCard.tsx`

### T-5.6 : Écran — CreateEventScreen (ou Dialog)

- [ ] Créer `ui/events/CreateEventScreen.kt` (ou `CreateEventDialog.kt`)
- [ ] Formulaire avec champs :
  - **Nom** (TextField, min 3 chars)
  - **Description** (TextField multiline)
  - **Date/Heure** (DatePicker + TimePicker Material 3)
  - **Lieu** (TextField, min 3 chars)
  - **Type** (Dropdown : PARTY, BIRTHDAY, DINER, SNACK)
- [ ] Validation en temps réel avec messages d'erreur
- [ ] Bouton submit avec loading state
- [ ] Retour à la liste après création réussie

> **Ref web** : `src/features/events/components/CreateEventForm.tsx`

### T-5.7 : Écran — EventDetailScreen

- [ ] Créer `ui/events/EventDetailScreen.kt`
- [ ] Afficher toutes les infos de l'événement
- [ ] Section Participants (intégré en phase 6)
- [ ] Section Resources (intégré en phase 7)
- [ ] Boutons d'action : Éditer, Supprimer (si organisateur)
- [ ] Confirmation avant suppression (AlertDialog)
- [ ] Navigation retour

> **Ref web** : `src/features/events/views/EventDetailPage.tsx`

### T-5.8 : Écran — Auth Screens

- [ ] Créer `ui/auth/WelcomeScreen.kt`
  - Logo/branding HappyRow
  - Bouton "Créer un compte"
  - Bouton "Se connecter"

> **Ref web** : `src/features/welcome/WelcomeView.tsx`

- [ ] Créer `ui/auth/LoginScreen.kt`
  - Champs email + password
  - Bouton "Se connecter"
  - Bouton "Connexion Google"
  - Lien "Mot de passe oublié ?"
  - Lien "Créer un compte"

> **Ref web** : `src/features/auth/components/LoginForm.tsx`

- [ ] Créer `ui/auth/RegisterScreen.kt`
  - Champs : firstname, lastname, email, password, confirmPassword
  - Bouton "S'inscrire"
  - Bouton "Inscription Google"
  - Lien "Déjà un compte ?"

> **Ref web** : `src/features/auth/components/RegisterForm.tsx`

- [ ] Créer `ui/auth/ForgotPasswordScreen.kt`
  - Champ email
  - Bouton "Réinitialiser"
  - Message de confirmation

> **Ref web** : `src/features/auth/components/ForgotPasswordForm.tsx`

### T-5.9 : Écran — ProfileScreen

- [ ] Créer `ui/profile/ProfileScreen.kt`
- [ ] Afficher : avatar, nom complet, email, statut de vérification, date d'inscription
- [ ] Bouton déconnexion

> **Ref web** : `src/features/user/UserProfilePage.tsx`

---

## Critères de validation

- La liste des événements se charge et s'affiche correctement
- La création d'un événement envoie la bonne requête API et rafraîchit la liste
- Le détail d'un événement affiche toutes les informations
- La suppression fonctionne avec confirmation
- Le DatePicker/TimePicker fonctionne correctement
- La validation du formulaire bloque la soumission si invalide
- Les états de chargement et d'erreur sont gérés visuellement
- La navigation entre les écrans fonctionne (back stack correct)
