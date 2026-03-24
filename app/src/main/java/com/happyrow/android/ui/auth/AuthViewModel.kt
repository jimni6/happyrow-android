package com.happyrow.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyrow.android.domain.model.AuthSession
import com.happyrow.android.domain.model.User
import com.happyrow.android.domain.model.UserCredentials
import com.happyrow.android.domain.model.UserRegistration
import com.happyrow.android.domain.repository.AuthRepository
import com.happyrow.android.usecases.auth.GetCurrentUser
import com.happyrow.android.usecases.auth.RegisterUser
import com.happyrow.android.usecases.auth.ResetPassword
import com.happyrow.android.usecases.auth.SignInUser
import com.happyrow.android.usecases.auth.SignInWithProvider
import com.happyrow.android.usecases.auth.SignOutUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUser: SignInUser,
    private val registerUser: RegisterUser,
    private val signOutUser: SignOutUser,
    private val getCurrentUser: GetCurrentUser,
    private val resetPassword: ResetPassword,
    private val signInWithProvider: SignInWithProvider,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    private var authStateUnsubscribe: (() -> Unit)? = null

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        authStateUnsubscribe = authRepository.onAuthStateChange { session ->
            if (session != null) {
                _authState.value = AuthState.Authenticated(session.user, session)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
        viewModelScope.launch {
            try {
                val session = authRepository.getCurrentSession()
                if (session != null) {
                    _authState.value = AuthState.Authenticated(session.user, session)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                handleSessionError(e)
            }
        }
    }

    private suspend fun handleSessionError(e: Exception) {
        val message = e.message ?: ""
        if (message.contains("session_not_found") || message.contains("refresh_token_not_found")) {
            try { authRepository.signOut() } catch (_: Exception) {}
            _authState.value = AuthState.Unauthenticated
        } else {
            try {
                authRepository.refreshSession()
                val session = authRepository.getCurrentSession()
                if (session != null) {
                    _authState.value = AuthState.Authenticated(session.user, session)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (_: Exception) {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState(isLoading = true)
            try {
                signInUser(UserCredentials(email, password))
                _loginUiState.value = LoginUiState()
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState(error = e.message ?: "Login failed")
            }
        }
    }

    fun register(registration: UserRegistration) {
        viewModelScope.launch {
            _registerUiState.value = RegisterUiState(isLoading = true)
            try {
                registerUser(registration)
                _registerUiState.value = RegisterUiState(registrationSuccess = true)
            } catch (e: Exception) {
                _registerUiState.value = RegisterUiState(error = e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                signOutUser()
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign out failed")
            }
        }
    }

    fun resetUserPassword(email: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState(isLoading = true)
            try {
                resetPassword(email)
                _loginUiState.value = LoginUiState()
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState(error = e.message ?: "Password reset failed")
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState(isLoading = true)
            try {
                signInWithProvider()
                _loginUiState.value = LoginUiState()
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState(error = e.message ?: "Google sign-in failed")
            }
        }
    }

    fun clearLoginError() {
        _loginUiState.value = _loginUiState.value.copy(error = null)
    }

    fun clearRegisterError() {
        _registerUiState.value = _registerUiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        authStateUnsubscribe?.invoke()
    }
}
