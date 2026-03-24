# Phase 7 — Feature Resources

Gestion des ressources par événement : création, liste, édition, suppression. Les ressources représentent ce qui est nécessaire pour un événement (nourriture, boissons, ustensiles, décorations, etc.).

> **Source web** : `happyrow-front/src/features/resources/`

---

## Tâches

### T-7.1 : Use Cases Resources

- [ ] Créer `usecases/resources/CreateResource.kt`
  - Validation : eventId non vide, name ≥ 2 chars, category valide, quantity > 0
  - Appeler `resourceRepository.createResource()`

- [ ] Créer `usecases/resources/GetResources.kt`
  - Validation : eventId non vide
  - Appeler `resourceRepository.getResourcesByEvent(eventId)`

- [ ] Créer `usecases/resources/UpdateResource.kt`
  - Validation : id non vide, au moins un champ modifié
  - Appeler `resourceRepository.updateResource(id, data)`

- [ ] Créer `usecases/resources/DeleteResource.kt`
  - Validation : id non vide
  - Appeler `resourceRepository.deleteResource(id)`

> **Ref web** : `src/features/resources/use-cases/` — 4 use cases

### T-7.2 : ResourcesViewModel

- [ ] Créer `ui/events/ResourcesViewModel.kt` (ou intégrer dans `EventDetailViewModel`)

```kotlin
@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val createResource: CreateResource,
    private val getResources: GetResources,
    private val updateResource: UpdateResource,
    private val deleteResource: DeleteResource
) : ViewModel() {

    private val _resourcesState = MutableStateFlow<ResourcesUiState>(ResourcesUiState.Loading)
    val resourcesState: StateFlow<ResourcesUiState> = _resourcesState.asStateFlow()

    fun loadResources(eventId: String) { /* ... */ }
    fun createResource(request: ResourceCreationRequest) { /* ... */ }
    fun updateResource(id: String, data: ResourceUpdateRequest) { /* ... */ }
    fun deleteResource(id: String) { /* ... */ }
}

sealed class ResourcesUiState {
    object Loading : ResourcesUiState()
    data class Success(val resources: List<Resource>) : ResourcesUiState()
    data class Error(val message: String) : ResourcesUiState()
}
```

### T-7.3 : Composant — `ResourceList`

- [ ] Créer `ui/events/components/ResourceList.kt`
- [ ] `LazyColumn` affichant chaque ressource avec :
  - Nom de la ressource
  - Catégorie (badge avec icône : 🍔 FOOD, 🥤 DRINK, 🍴 UTENSIL, 🎈 DECORATION, 📦 OTHER)
  - Barre de progression : quantité actuelle / quantité suggérée
  - Nombre de contributeurs
- [ ] État vide quand pas de ressources

> **Ref web** : `src/features/resources/components/ResourceList.tsx`

### T-7.4 : Composant — `ResourceItem`

- [ ] Créer `ui/events/components/ResourceItem.kt`
- [ ] Card Material 3 avec :
  - Nom + catégorie
  - `LinearProgressIndicator` pour currentQuantity/suggestedQuantity
  - Texte "X/Y apportés"
  - Actions : Éditer, Supprimer, Contribuer
- [ ] Confirmation avant suppression

> **Ref web** : `src/features/resources/components/ResourceItem.tsx`

### T-7.5 : Dialog — `AddResourceDialog`

- [ ] Créer `ui/events/components/AddResourceDialog.kt`
- [ ] Formulaire avec :
  - **Nom** (TextField, min 2 chars)
  - **Catégorie** (Dropdown : FOOD, DRINK, UTENSIL, DECORATION, OTHER)
  - **Quantité** (NumberField)
  - **Quantité suggérée** (NumberField, optionnel)
- [ ] Validation en temps réel
- [ ] Loading state sur le bouton

> **Ref web** : `src/features/resources/components/AddResourceForm.tsx`

### T-7.6 : Dialog — `EditResourceDialog`

- [ ] Créer `ui/events/components/EditResourceDialog.kt`
- [ ] Même formulaire que AddResourceDialog, pré-rempli avec les valeurs existantes
- [ ] Bouton "Enregistrer" avec loading state

### T-7.7 : Intégrer dans EventDetailScreen

- [ ] Ajouter la section "Ressources" dans `EventDetailScreen`
- [ ] Bouton "Ajouter une ressource" qui ouvre le dialog
- [ ] Afficher le compteur de ressources et la progression globale
- [ ] Intégrer `ResourceList` dans le scroll (après la section Participants)

---

## API Endpoints (rappel)

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/events/{eventId}/resources` | Créer une ressource |
| GET | `/events/{eventId}/resources` | Liste des ressources d'un événement |
| GET | `/resources/{id}` | Détail d'une ressource |
| PUT | `/resources/{id}` | Mettre à jour une ressource |
| DELETE | `/resources/{id}` | Supprimer une ressource |

> **Ref web** : `src/features/resources/services/HttpResourceRepository.ts`

---

## Critères de validation

- La liste des ressources se charge pour un événement donné
- La création d'une ressource fonctionne et rafraîchit la liste
- L'édition met à jour les données et rafraîchit
- La suppression fonctionne avec confirmation
- La barre de progression reflète correctement currentQuantity/suggestedQuantity
- Les catégories sont affichées avec les bons badges/icônes
- Les états de chargement et d'erreur sont gérés visuellement
