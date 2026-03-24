# ✅ Checklists — Pré/Post implémentation

Utilise ces checklists **à chaque tâche** pour garantir la qualité et la cohérence.

---

## Checklist pré-implémentation (avant de coder)

### Contexte
- [ ] J'ai lu la description de la tâche dans le fichier `XX-*.md`
- [ ] J'ai lu le fichier TypeScript source référencé (`> **Ref web**`) si disponible
- [ ] J'ai identifié les tâches précédentes dont celle-ci dépend
- [ ] Ces tâches précédentes sont bien terminées et cochées `[x]`

### Planification
- [ ] Je sais quels fichiers je vais créer ou modifier
- [ ] Je sais dans quel package chaque fichier va
- [ ] Je connais les imports nécessaires (pas de devinette)

---

## Checklist post-implémentation (après avoir codé)

### Compilation
- [ ] `./gradlew assembleDebug` compile sans erreur
- [ ] Pas de warnings non résolus dans mes fichiers

### Conventions
- [ ] Nommage conforme à `agent-conventions.md` (PascalCase classes, camelCase variables, etc.)
- [ ] Les fichiers sont dans le bon package
- [ ] Pas d'import `android.*` dans `domain/` ou `usecases/`
- [ ] Les DTO utilisent `snake_case` pour les champs JSON
- [ ] Les domain models utilisent `camelCase`

### Architecture
- [ ] Le flux de dépendance est correct (UI → UseCase → Domain ← Data)
- [ ] Les use cases utilisent `operator fun invoke`
- [ ] Les use cases ont `@Inject constructor`
- [ ] Les ViewModels ont `@HiltViewModel` + `@Inject constructor`
- [ ] Les repository impls ont `@Inject constructor`
- [ ] L'état UI utilise `StateFlow` (pas `LiveData`)
- [ ] Les composables reçoivent des **callbacks** de navigation (pas de `navController`)

### Patterns
- [ ] Les validations sont dans les **use cases** (pas dans le ViewModel ni le composable)
- [ ] Les erreurs HTTP 404 retournent `null` dans les repositories
- [ ] Les appels réseau sont wrappés dans `try/catch` dans le ViewModel
- [ ] Les mappers sont des **extension functions** dans des fichiers dédiés

### Tests (si applicable)
- [ ] Les tests existants passent toujours (`./gradlew testDebugUnitTest`)
- [ ] Si j'ai créé un use case → j'ai prévu un test (ou c'est planifié en Phase 9)
- [ ] Je n'ai supprimé aucun test existant

### Git
- [ ] Commit message au format : `feat(T-X.Y): <description>`
- [ ] Un commit par tâche logique
- [ ] La tâche est cochée `[x]` dans le fichier `.md`

---

## Checklist par type de fichier

### Domain Model (`domain/model/*.kt`)
- [ ] Data class avec tous les champs
- [ ] Types Kotlin idiomatiques (`Long` pour timestamps, `String` pour IDs)
- [ ] Valeurs par défaut pour les champs optionnels (`= null`, `= emptyList()`)
- [ ] Pas de dépendance Android
- [ ] Enums dans le même fichier ou fichier dédié si utilisé par plusieurs modèles

### Repository Interface (`domain/repository/*.kt`)
- [ ] Interface pure (pas de classe)
- [ ] Toutes les fonctions sont `suspend`
- [ ] Types de retour = domain models (jamais de DTOs)
- [ ] `List<X>` pour les collections (pas de `Array`)
- [ ] `X?` (nullable) pour les requêtes par ID qui peuvent ne rien trouver
- [ ] Pas de dépendance Android

### DTO (`data/remote/dto/*.kt`)
- [ ] Annotation `@Serializable`
- [ ] Champs en `snake_case` (convention backend)
- [ ] Champs optionnels avec `= null` ou valeurs par défaut
- [ ] Séparation `*ApiRequest` et `*ApiResponse`

### Retrofit Interface (`data/remote/api/*.kt`)
- [ ] Annotations HTTP correctes (`@GET`, `@POST`, `@PUT`, `@DELETE`)
- [ ] `@Path` pour les paramètres d'URL
- [ ] `@Body` pour le corps de requête
- [ ] Toutes les fonctions sont `suspend`
- [ ] Types de retour = DTOs (pas de domain models)

### Mapper (`data/remote/mapper/*.kt`)
- [ ] Extension functions : `fun XxxApiResponse.toDomain(): Xxx`
- [ ] Extension functions : `fun XxxCreationRequest.toApiRequest(): XxxApiRequest`
- [ ] Tous les champs sont mappés (pas d'oubli)
- [ ] Gestion des champs optionnels/nullable
- [ ] Conversion des enums (string → enum) avec gestion d'erreur

### Repository Impl (`data/repository/*.kt`)
- [ ] Implémente l'interface du domain
- [ ] `@Inject constructor`
- [ ] Utilise les mappers pour les conversions
- [ ] HTTP 404 → `null` (pas d'exception)
- [ ] Autres HTTP errors → propagées

### Use Case (`usecases/*/*.kt`)
- [ ] `@Inject constructor`
- [ ] `suspend operator fun invoke(...)`
- [ ] Validations avec `require()`
- [ ] Un seul appel repository
- [ ] Pas d'import Android
- [ ] Pas de try/catch (laisser remonter)

### ViewModel (`ui/*/*.kt`)
- [ ] `@HiltViewModel` + `@Inject constructor`
- [ ] `private val _state` + `val state: StateFlow`
- [ ] Actions dans `viewModelScope.launch`
- [ ] `try/catch(Exception)` autour des appels use case
- [ ] Mapping erreur → `UiState.Error(message)`
- [ ] Injecte les **use cases** (pas les repositories)

### Composable Screen (`ui/*/*.kt`)
- [ ] `hiltViewModel()` pour l'injection du ViewModel
- [ ] `collectAsStateWithLifecycle()` pour observer l'état
- [ ] `when` exhaustif sur les sealed class d'état
- [ ] `Scaffold` pour la structure (topBar, FAB, content)
- [ ] Navigation via callbacks (pas de `navController`)
- [ ] Gestion des 3 états : Loading, Success, Error
- [ ] `modifier: Modifier = Modifier` si composant réutilisable

### Hilt Module (`di/*.kt`)
- [ ] `@Module` + `@InstallIn(SingletonComponent::class)`
- [ ] `@Provides @Singleton` pour les instances
- [ ] `@Binds @Singleton` pour les bindings interface → impl
- [ ] Pas de logique métier dans les modules

---

## Red flags — Signes que quelque chose ne va pas

| Signal | Problème probable |
|--------|-------------------|
| Import `android.*` dans `domain/` | Violation Clean Architecture |
| `navController` en paramètre d'un composable screen | Navigation mal gérée |
| `LiveData` au lieu de `StateFlow` | Mauvais choix de state management |
| Repository injecté directement dans ViewModel | Use case manquant |
| Validation dans le ViewModel | Devrait être dans le use case |
| Try/catch dans un use case | Devrait être dans le ViewModel |
| DTO dans le domain | Mapper manquant |
| String hardcodée dans un composable | Devrait être dans `strings.xml` |
| `var` mutable dans un data class | Les states doivent être immutables |
