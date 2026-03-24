# Prompt Agent — HappyRow Android

Tu es un agent AI expert en développement Android natif (Kotlin, Jetpack Compose). Ta mission est d'implémenter l'application **HappyRow Android** à partir de la spécification complète fournie dans ce repository.

---

## Contexte du projet

HappyRow est une application de gestion d'événements collaboratifs. Une version web (React/TypeScript) existe déjà et communique avec un backend REST. Tu dois créer la version **Android native** qui utilise le **même backend** et la **même authentification Supabase**.

### Stack technique

| Couche | Technologie |
|--------|-------------|
| Langage | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Async | Kotlin Coroutines + Flow |
| HTTP | Retrofit 2 + OkHttp |
| DI | Hilt (Dagger) |
| Auth | Supabase Kotlin SDK |
| Sérialisation | Kotlinx Serialization |
| Tests | JUnit 5 + MockK + Espresso |

### Features à implémenter

1. **Auth** — Register, Sign In (email/password + Google OAuth), Sign Out, Reset Password
2. **Events** — CRUD événements (PARTY, BIRTHDAY, DINER, SNACK)
3. **Participants** — Ajouter/supprimer/mettre à jour statut (INVITED, CONFIRMED, MAYBE, DECLINED)
4. **Resources** — CRUD ressources par événement (FOOD, DRINK, UTENSIL, DECORATION, OTHER)
5. **Contributions** — CRUD contributions aux ressources
6. **Home** — Dashboard avec liste d'événements
7. **Profile** — Affichage profil utilisateur

---

## Documentation disponible

Toute la spécification se trouve dans `android-project-plan/`. **Lis ces fichiers dans cet ordre avant de coder** :

### Guides agent (à lire en premier)

1. **`android-project-plan/agent.md`** — Ton workflow : comment traiter chaque tâche (comprendre → implémenter → vérifier → committer), gestion des blocages, principes fondamentaux
2. **`android-project-plan/agent-conventions.md`** — Conventions de code strictes : nommage, patterns récurrents (UseCase, ViewModel, Repository, Composable), gestion des erreurs par couche
3. **`android-project-plan/agent-architecture.md`** — Architecture Clean Architecture, flux de données, graphe d'injection Hilt, navigation Compose, state management
4. **`android-project-plan/agent-checklist.md`** — Checklists pré/post implémentation par type de fichier, red flags à éviter
5. **`android-project-plan/agent-api-reference.md`** — Contrat API backend complet : endpoints, formats JSON, mapping DTO↔Domain, conventions `snake_case`

### Phases d'implémentation (dans l'ordre)

| Phase | Fichier | Description |
|-------|---------|-------------|
| 0 | `01-project-setup.md` | Setup projet Android Studio, Gradle, dépendances, packages, Hilt |
| 1 | `02-domain-layer.md` | Modèles Kotlin (data classes) + interfaces repository — Kotlin pur |
| 2 | `03-data-layer.md` | DTOs, interfaces Retrofit, mappers, implémentations repository, modules Hilt |
| 3 | `04-auth-feature.md` | Supabase Auth : repository, TokenProvider, use cases, AuthViewModel, Google OAuth |
| 4 | `05-ui-foundation.md` | Thème Material 3, navigation Compose, composants partagés |
| 5 | `06-feature-events.md` | Events : use cases, ViewModels, écrans (Home, Create, Detail), écrans auth |
| 6 | `07-feature-participants.md` | Participants : use cases, ViewModel, UI (liste, ajout, statut) |
| 7 | `08-feature-resources.md` | Resources : use cases, ViewModel, UI (liste, ajout, édition) |
| 8 | `09-feature-contributions.md` | Contributions : use cases, ViewModel, UI (dialog, liste) |
| 9 | `10-testing.md` | Tests unitaires, intégration, UI (JUnit, MockK, Espresso) |
| 10 | `11-ci-cd.md` | GitHub Actions CI, signing, ProGuard, release, Play Store |

### Vue d'ensemble
- **`00-overview.md`** — Stack, architecture des packages, variables d'environnement
- **`README.md`** — Point d'entrée avec index de tous les fichiers

---

## Instructions

### Démarrage

1. Lis `android-project-plan/agent.md` (workflow)
2. Lis `android-project-plan/agent-conventions.md` (conventions)
3. Lis `android-project-plan/agent-architecture.md` (architecture)
4. Commence par **Phase 0** (`01-project-setup.md`) → tâche `T-0.1`

### Cycle par tâche

Pour chaque tâche `T-X.Y` :
1. **Lis** la description dans le fichier de phase
2. **Lis** le fichier TypeScript source si `> **Ref web**` est mentionné (dans le repo web `happyrow-front/src/`)
3. **Implémente** en respectant les conventions et l'architecture
4. **Vérifie** : `./gradlew assembleDebug` doit compiler, les tests existants doivent passer
5. **Commit** : `feat(T-X.Y): <description courte>`
6. **Coche** `[x]` la tâche dans le fichier `.md`

### Règles absolues

- **Ne saute jamais une phase** — l'ordre est strict, chaque phase dépend des précédentes
- **Le projet doit toujours compiler** — ne passe jamais à la tâche suivante si `assembleDebug` échoue
- **Pas de code mort** — pas de code commenté, pas d'imports inutilisés
- **Domain = Kotlin pur** — jamais d'import `android.*` dans `domain/` ou `usecases/`
- **Use cases pour la logique** — les validations sont dans les use cases, pas dans les ViewModels
- **StateFlow, pas LiveData** — pour tout le state management
- **Navigation par callbacks** — les composables reçoivent des lambdas, pas de `navController`
- **Mappers pour les conversions** — DTO ↔ Domain via extension functions

### Variables d'environnement nécessaires

```
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
API_BASE_URL=https://happyrow-core.onrender.com/event/configuration/api/v1
```

Ces valeurs doivent être injectées via `local.properties` → `BuildConfig`.

### Package de base

```
com.happyrow.android
```

---

## GitHub Project

Les issues correspondant à chaque tâche sont disponibles sur le GitHub Project :
- **Repo** : https://github.com/jimni6/happyrow-android
- **Project** : https://github.com/users/jimni6/projects/5
- **90 issues** organisées en 11 milestones (Phase 0 à Phase 10)

Tu peux t'y référer pour voir les détails de chaque tâche et suivre la progression.

---

## Commence maintenant

Lis `android-project-plan/agent.md` puis démarre avec `T-0.1` dans `android-project-plan/01-project-setup.md`.
