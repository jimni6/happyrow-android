# Phase 8 — Feature Contributions

Gestion des contributions aux ressources : un utilisateur peut contribuer une quantité à une ressource d'un événement.

> **Source web** : `happyrow-front/src/features/contributions/`

---

## Tâches

### T-8.1 : Use Cases Contributions

- [ ] Créer `usecases/contributions/AddContribution.kt`
  - Validation : eventId, resourceId, userId non vides, quantity > 0
  - Appeler `contributionRepository.createContribution()`

- [ ] Créer `usecases/contributions/GetContributions.kt`
  - Validation : eventId, resourceId non vides
  - Appeler `contributionRepository.getContributionsByResource(eventId, resourceId)`

- [ ] Créer `usecases/contributions/UpdateContribution.kt`
  - Validation : eventId, resourceId non vides, quantity > 0
  - Appeler `contributionRepository.updateContribution(eventId, resourceId, data)`

- [ ] Créer `usecases/contributions/DeleteContribution.kt`
  - Validation : eventId, resourceId non vides
  - Appeler `contributionRepository.deleteContribution(eventId, resourceId)`

> **Ref web** : `src/features/contributions/use-cases/` — 4 use cases

### T-8.2 : ContributionsViewModel

- [ ] Créer `ui/events/ContributionsViewModel.kt` (ou intégrer dans `ResourcesViewModel`)

```kotlin
@HiltViewModel
class ContributionsViewModel @Inject constructor(
    private val addContribution: AddContribution,
    private val getContributions: GetContributions,
    private val updateContribution: UpdateContribution,
    private val deleteContribution: DeleteContribution
) : ViewModel() {

    private val _contributionsState = MutableStateFlow<ContributionsUiState>(ContributionsUiState.Loading)
    val contributionsState: StateFlow<ContributionsUiState> = _contributionsState.asStateFlow()

    fun loadContributions(eventId: String, resourceId: String) { /* ... */ }
    fun contribute(request: ContributionCreationRequest) { /* ... */ }
    fun updateContribution(eventId: String, resourceId: String, data: ContributionUpdateRequest) { /* ... */ }
    fun deleteContribution(eventId: String, resourceId: String) { /* ... */ }
}

sealed class ContributionsUiState {
    object Loading : ContributionsUiState()
    data class Success(val contributions: List<Contribution>) : ContributionsUiState()
    data class Error(val message: String) : ContributionsUiState()
}
```

### T-8.3 : Dialog — `ContributeDialog`

- [ ] Créer `ui/events/components/ContributeDialog.kt`
- [ ] Formulaire simple :
  - Nom de la ressource (affiché, non éditable)
  - Quantité actuelle / suggérée (affiché)
  - **Quantité à contribuer** (NumberField, min 1)
- [ ] Bouton "Contribuer" avec loading state
- [ ] Message de succès après contribution

### T-8.4 : Composant — `ContributionsList`

- [ ] Créer `ui/events/components/ContributionsList.kt`
- [ ] Liste des contributions pour une ressource donnée
- [ ] Afficher par contributeur : userId (ou email), quantité, date
- [ ] Permettre la modification/suppression de sa propre contribution

### T-8.5 : Intégrer dans ResourceItem

- [ ] Ajouter un bouton "Contribuer" sur chaque `ResourceItem`
- [ ] Ouvrir `ContributeDialog` au clic
- [ ] Mettre à jour `currentQuantity` après contribution réussie
- [ ] Optionnel : afficher la liste des contributeurs dans un expandable section

---

## API Endpoints (rappel)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/events/{eventId}/resources/{resourceId}/contributions` | Liste des contributions |
| POST | `/events/{eventId}/resources/{resourceId}/contributions` | Créer une contribution |
| DELETE | `/events/{eventId}/resources/{resourceId}/contributions` | Supprimer une contribution |

> **Note** : Le backend utilise POST pour update aussi (même endpoint que create).

> **Ref web** : `src/features/contributions/services/HttpContributionRepository.ts`

---

## Critères de validation

- La contribution à une ressource fonctionne et met à jour la quantité affichée
- La liste des contributions par ressource s'affiche correctement
- La suppression de sa propre contribution fonctionne
- La validation empêche de contribuer une quantité ≤ 0
- Les états de chargement et d'erreur sont gérés
