# Phase 6 — Feature Participants

Gestion des participants d'un événement : ajout, liste, mise à jour du statut, suppression.

> **Source web** : `happyrow-front/src/features/participants/`

---

## Tâches

### T-6.1 : Use Cases Participants

- [ ] Créer `usecases/participants/AddParticipant.kt`
  - Validation : eventId non vide, userEmail non vide et format email valide, status valide
  - Appeler `participantRepository.addParticipant()`

- [ ] Créer `usecases/participants/GetParticipants.kt`
  - Validation : eventId non vide
  - Appeler `participantRepository.getParticipantsByEvent(eventId)`

- [ ] Créer `usecases/participants/UpdateParticipantStatus.kt`
  - Validation : eventId, userEmail non vides, status valide
  - Appeler `participantRepository.updateParticipantStatus(eventId, userEmail, data)`

- [ ] Créer `usecases/participants/RemoveParticipant.kt`
  - Validation : eventId, userEmail non vides
  - Appeler `participantRepository.removeParticipant(eventId, userEmail)`

> **Ref web** : `src/features/participants/use-cases/` — 4 use cases

### T-6.2 : ParticipantsViewModel

- [ ] Créer `ui/events/ParticipantsViewModel.kt` (ou intégrer dans `EventDetailViewModel`)

```kotlin
@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val addParticipant: AddParticipant,
    private val getParticipants: GetParticipants,
    private val updateParticipantStatus: UpdateParticipantStatus,
    private val removeParticipant: RemoveParticipant
) : ViewModel() {

    private val _participantsState = MutableStateFlow<ParticipantsUiState>(ParticipantsUiState.Loading)
    val participantsState: StateFlow<ParticipantsUiState> = _participantsState.asStateFlow()

    fun loadParticipants(eventId: String) { /* ... */ }
    fun addParticipant(eventId: String, email: String, status: ParticipantStatus) { /* ... */ }
    fun updateStatus(eventId: String, userEmail: String, status: ParticipantStatus) { /* ... */ }
    fun removeParticipant(eventId: String, userEmail: String) { /* ... */ }
}

sealed class ParticipantsUiState {
    object Loading : ParticipantsUiState()
    data class Success(val participants: List<Participant>) : ParticipantsUiState()
    data class Error(val message: String) : ParticipantsUiState()
}
```

### T-6.3 : Composant — `ParticipantList`

- [ ] Créer `ui/events/components/ParticipantList.kt`
- [ ] `LazyColumn` affichant chaque participant avec :
  - Email (et nom si disponible)
  - Badge de statut coloré (INVITED=gris, CONFIRMED=vert, MAYBE=orange, DECLINED=rouge)
  - Date d'ajout formatée
- [ ] État vide quand pas de participants

> **Ref web** : `src/features/participants/components/ParticipantList.tsx`

### T-6.4 : Composant — `ParticipantItem`

- [ ] Créer `ui/events/components/ParticipantItem.kt`
- [ ] Afficher les infos du participant dans une Card/ListItem
- [ ] Menu contextuel (long press ou icône) :
  - Changer le statut (dropdown INVITED/CONFIRMED/MAYBE/DECLINED)
  - Supprimer le participant
- [ ] Confirmation avant suppression

> **Ref web** : `src/features/participants/components/ParticipantItem.tsx`

### T-6.5 : Dialog — `AddParticipantDialog`

- [ ] Créer `ui/events/components/AddParticipantDialog.kt`
- [ ] Champ email avec validation
- [ ] Sélecteur de statut initial (dropdown ou chips)
- [ ] Bouton Ajouter avec loading state
- [ ] Gestion d'erreur (email déjà participant, etc.)

> **Ref web** : `src/features/participants/components/AddParticipantForm.tsx`

### T-6.6 : Intégrer dans EventDetailScreen

- [ ] Ajouter la section "Participants" dans `EventDetailScreen`
- [ ] Bouton "Ajouter un participant" qui ouvre le dialog
- [ ] Afficher le compteur de participants
- [ ] Intégrer `ParticipantList` dans le scroll

---

## API Endpoints (rappel)

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/events/{eventId}/participants` | Ajouter un participant |
| GET | `/events/{eventId}/participants` | Liste des participants |
| PUT | `/events/{eventId}/participants/{userEmail}` | Mettre à jour le statut |
| DELETE | `/events/{eventId}/participants/{userEmail}` | Supprimer un participant |

> **Ref web** : `src/features/participants/services/HttpParticipantRepository.ts`

---

## Critères de validation

- La liste des participants se charge pour un événement donné
- L'ajout d'un participant fonctionne et rafraîchit la liste
- Le changement de statut met à jour le badge visuellement
- La suppression fonctionne avec confirmation
- La validation email empêche l'ajout d'emails invalides
- Les états de chargement et d'erreur sont gérés
