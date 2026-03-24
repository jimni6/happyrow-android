# HappyRow Android — Vue d'ensemble du projet

## Objectif

Recréer l'application web HappyRow (React/TypeScript) en application Android native avec Kotlin et Jetpack Compose. L'application communique avec le même backend REST et utilise Supabase pour l'authentification.

## Stack technique

| Couche | Technologie |
|--------|-------------|
| **Langage** | Kotlin |
| **SDK** | Android SDK (minSdk 26, targetSdk 35) |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Async** | Kotlin Coroutines + Flow |
| **HTTP** | Retrofit 2 + OkHttp |
| **DI** | Hilt (Dagger) |
| **Auth** | Supabase Kotlin SDK (GoTrue) |
| **Serialization** | Kotlinx Serialization |
| **Tests unitaires** | JUnit 5 + MockK |
| **Tests UI** | Espresso + Compose Testing |
| **Navigation** | Compose Navigation |

## Features à implémenter

Portées depuis la web app existante (`happyrow-front/src/features/`) :

1. **Auth** — Register, Sign In (email/password + Google OAuth), Sign Out, Reset Password, Session management
2. **Events** — CRUD événements (PARTY, BIRTHDAY, DINER, SNACK)
3. **Participants** — Ajouter/supprimer participants, mettre à jour statut (INVITED, CONFIRMED, MAYBE, DECLINED)
4. **Resources** — CRUD ressources par événement (FOOD, DRINK, UTENSIL, DECORATION, OTHER)
5. **Contributions** — CRUD contributions aux ressources
6. **Home** — Dashboard avec liste d'événements, accueil personnalisé
7. **User Profile** — Affichage et édition du profil utilisateur

## Backend API

- **Base URL** : `https://happyrow-core.onrender.com/event/configuration/api/v1`
- **Auth** : Supabase (URL + Anon Key via BuildConfig)
- **Format** : JSON, authentification Bearer token
- **Conventions backend** : `snake_case` pour les champs JSON, `identifier` au lieu de `id`, `creator` au lieu de `organizerId`, timestamps en millisecondes

## Architecture des packages

```
com.happyrow.android/
├── di/                          # Modules Hilt
├── domain/
│   ├── model/                   # Entités métier (data classes)
│   └── repository/              # Interfaces repository
├── data/
│   ├── remote/
│   │   ├── api/                 # Interfaces Retrofit
│   │   ├── dto/                 # DTOs (réponses/requêtes API)
│   │   └── mapper/              # DTO ↔ Domain mappers
│   └── repository/              # Implémentations repository
├── usecases/
│   ├── auth/
│   ├── events/
│   ├── participants/
│   ├── resources/
│   └── contributions/
└── ui/
    ├── theme/                   # Material 3 theme
    ├── navigation/              # NavHost + routes
    ├── components/              # Composables réutilisables
    ├── auth/                    # Écrans auth (Login, Register, ForgotPassword)
    ├── home/                    # Dashboard
    ├── events/                  # Liste + détail + création événements
    └── profile/                 # Profil utilisateur
```

## Ordre d'implémentation recommandé

Chaque fichier `XX-*.md` dans ce dossier décrit les tâches d'une phase :

| Phase | Fichier | Description |
|-------|---------|-------------|
| 0 | `01-project-setup.md` | Setup projet Android Studio, Gradle, dépendances |
| 1 | `02-domain-layer.md` | Modèles et interfaces repository |
| 2 | `03-data-layer.md` | DTOs, Retrofit, implémentations repository |
| 3 | `04-auth-feature.md` | Intégration Supabase Auth |
| 4 | `05-ui-foundation.md` | Theme Material 3, navigation, composants partagés |
| 5 | `06-feature-events.md` | Feature événements (UI + ViewModel) |
| 6 | `07-feature-participants.md` | Feature participants |
| 7 | `08-feature-resources.md` | Feature ressources |
| 8 | `09-feature-contributions.md` | Feature contributions |
| 9 | `10-testing.md` | Stratégie et tâches de tests |
| 10 | `11-ci-cd.md` | CI/CD et release |

## Variables d'environnement (BuildConfig)

```
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
API_BASE_URL=https://happyrow-core.onrender.com/event/configuration/api/v1
```

## Référence web app source

Le code source de la web app se trouve dans `/Users/j.ni/IdeaProjects/happyrow-front/src/`. Chaque tâche fait référence au fichier TypeScript source équivalent pour faciliter la traduction.
