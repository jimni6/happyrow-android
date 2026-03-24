# 📱 HappyRow Android — Plan de projet

Ce dossier contient le plan complet pour recréer l'application web HappyRow en application Android native avec Kotlin et Jetpack Compose.

## Pour les agents AI

**Ce dossier est conçu pour être utilisé par un agent AI qui implémentera le projet de manière itérative.** Chaque fichier `XX-*.md` représente une phase avec des tâches numérotées (`T-X.Y`). Les tâches sont à cocher au fur et à mesure de leur complétion.

### ⚠️ Lire en premier (guides agent)

Avant de coder, lis ces fichiers dans cet ordre :

| Fichier | Rôle |
|---------|------|
| `agent.md` | **Workflow principal** — comment traiter chaque tâche, cycle comprendre→implémenter→vérifier→committer |
| `agent-conventions.md` | **Conventions de code** — nommage, patterns (UseCase, ViewModel, Repository, Composable), gestion des erreurs |
| `agent-architecture.md` | **Architecture** — Clean Architecture, flux de données, injection Hilt, navigation, state management |
| `agent-checklist.md` | **Checklists** — pré/post implémentation, par type de fichier, red flags à éviter |
| `agent-api-reference.md` | **Contrat API** — tous les endpoints, formats JSON, mapping DTO↔Domain, conventions backend |

### Ordre d'exécution des phases

Suivre les phases **dans l'ordre numérique** — chaque phase dépend des précédentes :

```
00-overview.md          ← Lire en premier (contexte global)
01-project-setup.md     ← Phase 0 : Setup projet Android Studio
02-domain-layer.md      ← Phase 1 : Modèles Kotlin + interfaces
03-data-layer.md        ← Phase 2 : Retrofit, DTOs, implémentations
04-auth-feature.md      ← Phase 3 : Supabase Auth
05-ui-foundation.md     ← Phase 4 : Theme, navigation, composants
06-feature-events.md    ← Phase 5 : Events (UI + logique)
07-feature-participants.md ← Phase 6 : Participants
08-feature-resources.md    ← Phase 7 : Resources
09-feature-contributions.md ← Phase 8 : Contributions
10-testing.md           ← Phase 9 : Tests
11-ci-cd.md             ← Phase 10 : CI/CD et release
```

### Conventions

- Chaque tâche a un identifiant unique : `T-{phase}.{numéro}` (ex: `T-2.5`)
- `[ ]` = à faire, `[x]` = fait
- Les blocs de code sont des **suggestions**, pas du copier-coller — adapter selon le contexte
- Les `> **Ref web**` pointent vers le fichier TypeScript source équivalent dans `happyrow-front/src/`

### Contexte technique

- **Web app source** : `../src/` (React/TypeScript/Vite)
- **Backend API** : `https://happyrow-core.onrender.com/event/configuration/api/v1`
- **Auth** : Supabase (GoTrue)
- **Stack Android** : Kotlin, Jetpack Compose, MVVM, Clean Architecture, Coroutines, Retrofit, Hilt, JUnit + MockK + Espresso

### Projet Android cible

Le projet Android doit être créé **dans un nouveau repository séparé** (ex: `happyrow-android`), pas dans ce repo web.
