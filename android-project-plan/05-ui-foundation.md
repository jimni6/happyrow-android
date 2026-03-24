# Phase 4 — UI Foundation (Theme, Navigation, Composants partagés)

Setup du thème Material 3, du système de navigation Compose, et des composants UI réutilisables.

> **Source web** : `happyrow-front/src/core/styles/`, `happyrow-front/src/layouts/`, `happyrow-front/src/shared/`

---

## Tâches

### T-4.1 : Thème Material 3

- [ ] Créer `ui/theme/Color.kt` — palette de couleurs HappyRow
- [ ] Créer `ui/theme/Type.kt` — typographie (Material 3)
- [ ] Créer `ui/theme/Theme.kt` — `HappyRowTheme` composable

```kotlin
@Composable
fun HappyRowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme(...) else lightColorScheme(...)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HappyRowTypography,
        content = content
    )
}
```

- [ ] Reprendre les couleurs principales de la web app (gradients, accents)
- [ ] Support dark mode

### T-4.2 : Navigation — Routes

- [ ] Créer `ui/navigation/Routes.kt` — définir toutes les routes

```kotlin
sealed class Route(val route: String) {
    // Auth
    object Welcome : Route("welcome")
    object Login : Route("login")
    object Register : Route("register")
    object ForgotPassword : Route("forgot-password")

    // Main (authentifié)
    object Home : Route("home")
    object EventDetail : Route("events/{eventId}") {
        fun createRoute(eventId: String) = "events/$eventId"
    }
    object Profile : Route("profile")
}
```

### T-4.3 : Navigation — NavHost

- [ ] Créer `ui/navigation/HappyRowNavHost.kt`

```kotlin
@Composable
fun HappyRowNavHost(
    navController: NavHostController,
    authState: AuthState
) {
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Route.Home.route
        else -> Route.Welcome.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        // Auth flow
        composable(Route.Welcome.route) { WelcomeScreen(...) }
        composable(Route.Login.route) { LoginScreen(...) }
        composable(Route.Register.route) { RegisterScreen(...) }
        composable(Route.ForgotPassword.route) { ForgotPasswordScreen(...) }

        // Main flow (authentifié)
        composable(Route.Home.route) { HomeScreen(...) }
        composable(
            Route.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            EventDetailScreen(eventId = eventId, ...)
        }
        composable(Route.Profile.route) { ProfileScreen(...) }
    }
}
```

> **Ref web** : `src/App.tsx` lignes 186-198 — routing React

### T-4.4 : Mettre à jour `MainActivity`

- [ ] Intégrer le `NavHost` et le `AuthViewModel`

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HappyRowTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.authState.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                HappyRowNavHost(
                    navController = navController,
                    authState = authState
                )
            }
        }
    }
}
```

### T-4.5 : Composant partagé — `AppTopBar`

- [ ] Créer `ui/components/AppTopBar.kt`
- [ ] Barre de navigation sticky avec :
  - Logo / titre "HappyRow"
  - Salutation utilisateur (prénom)
  - Bouton déconnexion
  - Navigation retour quand nécessaire

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String = "HappyRow",
    user: User? = null,
    onSignOut: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        },
        actions = {
            if (user != null) {
                Text("Bonjour, ${user.firstname}")
                IconButton(onClick = onSignOut) {
                    Icon(Icons.Default.ExitToApp, "Sign out")
                }
            }
        }
    )
}
```

> **Ref web** : `src/layouts/AppLayout.tsx` — header de l'app

### T-4.6 : Composant partagé — `LoadingScreen`

- [ ] Créer `ui/components/LoadingScreen.kt`
- [ ] Spinner centré avec message optionnel

### T-4.7 : Composant partagé — `ErrorMessage`

- [ ] Créer `ui/components/ErrorMessage.kt`
- [ ] Affichage d'erreur réutilisable avec bouton retry optionnel

### T-4.8 : Composant partagé — `UserAvatar`

- [ ] Créer `ui/components/UserAvatar.kt`
- [ ] Cercle avec initiales du user (firstname + lastname)
- [ ] Couleur générée à partir du nom

> **Ref web** : `src/features/home/HomeScreen.tsx` — avatar avec initiales

### T-4.9 : Composant partagé — `HappyRowButton`

- [ ] Créer `ui/components/HappyRowButton.kt`
- [ ] Bouton primaire et secondaire stylés
- [ ] Support loading state (disabled + spinner)

### T-4.10 : Composant partagé — `InputField`

- [ ] Créer `ui/components/InputField.kt`
- [ ] Champ de texte Material 3 avec :
  - Label
  - Message d'erreur
  - Icône optionnelle
  - Mode password (visibility toggle)

### T-4.11 : Scaffold principal avec BottomBar (optionnel)

- [ ] Évaluer si une bottom navigation est pertinente (Home, Events, Profile)
- [ ] Si oui, créer `ui/navigation/BottomNavBar.kt`

---

## Critères de validation

- Le thème s'applique correctement (couleurs, typographie)
- La navigation fonctionne entre toutes les routes
- Le redirect automatique vers Welcome (non-auth) ou Home (auth) fonctionne
- Les composants partagés sont réutilisables et suivent Material 3
- Le TopAppBar affiche le prénom de l'utilisateur
- Le dark mode fonctionne
