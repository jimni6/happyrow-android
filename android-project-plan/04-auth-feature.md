# Phase 3 — Auth Feature (Supabase)

Intégration complète de l'authentification via Supabase Kotlin SDK + use cases + ViewModel.

> **Source web** : `happyrow-front/src/features/auth/`

---

## Contexte

La web app utilise `@supabase/supabase-js` pour l'auth. Côté Android, on utilise le **Supabase Kotlin SDK** (`io.github.jan-tennert.supabase:gotrue-kt`). Les concepts sont identiques : GoTrue pour email/password, OAuth via Chrome Custom Tabs pour Google.

---

## Tâches

### T-3.1 : Implémentation `SupabaseAuthRepository`

- [ ] Créer `data/repository/SupabaseAuthRepository.kt`
- [ ] Injecter le client Supabase via Hilt
- [ ] Implémenter toutes les méthodes de `AuthRepository` :

| Méthode | Supabase Kotlin SDK |
|---------|-------------------|
| `register()` | `supabase.auth.signUpWith(Email) { email, password, data }` |
| `signIn()` | `supabase.auth.signInWith(Email) { email, password }` |
| `signOut()` | `supabase.auth.signOut()` |
| `getCurrentUser()` | `supabase.auth.currentUserOrNull()` |
| `getCurrentSession()` | `supabase.auth.currentSessionOrNull()` |
| `refreshSession()` | `supabase.auth.refreshCurrentSession()` |
| `resetPassword()` | `supabase.auth.resetPasswordForEmail(email)` |
| `updatePassword()` | `supabase.auth.updateUser { password }` |
| `signInWithProvider("google")` | `supabase.auth.signInWith(Google)` via Chrome Custom Tabs |
| `onAuthStateChange()` | `supabase.auth.sessionStatus` (Flow) |

- [ ] Mapper les objets Supabase → domain `User` et `AuthSession`
- [ ] Stocker `firstname`/`lastname` dans `user_metadata` lors du register
- [ ] Extraire `firstname`/`lastname` depuis `user_metadata` lors du mapping

> **Ref web** : `src/features/auth/services/SupabaseAuthRepository.ts` — logique complète

### T-3.2 : Module Hilt — AuthModule

- [ ] Créer `di/AuthModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
        }
    }

    @Provides
    @Singleton
    fun provideAuthRepository(supabaseClient: SupabaseClient): AuthRepository {
        return SupabaseAuthRepository(supabaseClient)
    }
}
```

### T-3.3 : TokenProvider pour Retrofit

- [ ] Créer un `TokenProvider` qui lit le token depuis la session Supabase courante
- [ ] L'injecter dans `AuthInterceptor` (créé en T-2.10)
- [ ] Le token doit être rafraîchi automatiquement si expiré

```kotlin
class TokenProvider @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    fun getToken(): String? {
        return supabaseClient.auth.currentSessionOrNull()?.accessToken
    }
}
```

### T-3.4 : Use Cases Auth

- [ ] Créer `usecases/auth/RegisterUser.kt`
  - Validation : email non vide, password ≥ 8 chars, firstname/lastname ≥ 2 chars, confirmPassword match, format email regex
  - Appeler `authRepository.register()`

> **Ref web** : `src/features/auth/use-cases/RegisterUser.ts` — validation complète lignes 15-58

- [ ] Créer `usecases/auth/SignInUser.kt`
  - Validation : email et password non vides
  - Appeler `authRepository.signIn()`

- [ ] Créer `usecases/auth/SignOutUser.kt`
  - Appeler `authRepository.signOut()`

- [ ] Créer `usecases/auth/GetCurrentUser.kt`
  - Appeler `authRepository.getCurrentUser()`

- [ ] Créer `usecases/auth/ResetPassword.kt`
  - Validation : email non vide, format email valide
  - Appeler `authRepository.resetPassword()`

- [ ] Créer `usecases/auth/SignInWithProvider.kt`
  - Appeler `authRepository.signInWithProvider("google")`

### T-3.5 : AuthViewModel

- [ ] Créer `ui/auth/AuthViewModel.kt`

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUser: RegisterUser,
    private val signInUser: SignInUser,
    private val signOutUser: SignOutUser,
    private val getCurrentUser: GetCurrentUser,
    private val resetPassword: ResetPassword,
    private val signInWithProvider: SignInWithProvider,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        // Écouter les changements d'état auth via le Flow Supabase
    }

    fun login(email: String, password: String) { /* ... */ }
    fun register(registration: UserRegistration) { /* ... */ }
    fun signOut() { /* ... */ }
    fun resetPassword(email: String) { /* ... */ }
    fun signInWithGoogle() { /* ... */ }
}
```

- [ ] Définir les sealed classes d'état :

```kotlin
sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User, val session: AuthSession) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false
)
```

### T-3.6 : Configuration Google OAuth (Chrome Custom Tabs)

- [ ] Ajouter la dépendance `androidx.browser:browser:1.8.0`
- [ ] Configurer le deep link dans `AndroidManifest.xml` pour le callback OAuth
- [ ] Configurer le redirect URL dans le dashboard Supabase pour Android

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="com.happyrow.android" android:host="callback" />
</intent-filter>
```

### T-3.7 : Gestion de session persistante

- [ ] Supabase Kotlin SDK gère le stockage de session automatiquement
- [ ] Vérifier que la session est restaurée au redémarrage de l'app
- [ ] Implémenter le refresh automatique du token si expiré
- [ ] Gérer les erreurs `session_not_found` et `refresh_token_not_found` gracefully (rediriger vers login)

> **Ref web** : `SupabaseAuthRepository.ts` lignes 92-143 — gestion des erreurs de session

---

## Critères de validation

- Register avec email/password fonctionne (vérifier dans le dashboard Supabase)
- Sign in retourne un `AuthSession` valide
- Sign out efface la session
- Le token est automatiquement injecté dans les requêtes Retrofit
- Google OAuth lance Chrome Custom Tabs et revient dans l'app
- La session persiste après redémarrage de l'app
- Les erreurs de session expirée sont gérées proprement
