package com.happyrow.android.data.repository

import com.happyrow.android.domain.model.AuthSession
import com.happyrow.android.domain.model.User
import com.happyrow.android.domain.model.UserCredentials
import com.happyrow.android.domain.model.UserRegistration
import com.happyrow.android.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class SupabaseAuthRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    override suspend fun register(userData: UserRegistration): User {
        supabaseClient.auth.signUpWith(Email) {
            email = userData.email
            password = userData.password
            data = buildJsonObject {
                put("firstname", userData.firstname)
                put("lastname", userData.lastname)
            }
        }
        return getCurrentUser() ?: throw IllegalStateException("Registration failed: no user returned")
    }

    override suspend fun signIn(credentials: UserCredentials): AuthSession {
        supabaseClient.auth.signInWith(Email) {
            email = credentials.email
            password = credentials.password
        }
        return getCurrentSession() ?: throw IllegalStateException("Sign-in failed: no session returned")
    }

    override suspend fun signOut() {
        supabaseClient.auth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        val user = supabaseClient.auth.currentUserOrNull() ?: return null
        return user.toDomainUser()
    }

    override suspend fun getCurrentSession(): AuthSession? {
        val session = supabaseClient.auth.currentSessionOrNull() ?: return null
        val user = session.user ?: return null
        return AuthSession(
            user = user.toDomainUser(),
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
            expiresAt = session.expiresAt?.epochSeconds ?: 0L
        )
    }

    override suspend fun refreshSession(): AuthSession {
        supabaseClient.auth.refreshCurrentSession()
        return getCurrentSession() ?: throw IllegalStateException("Session refresh failed")
    }

    override suspend fun resetPassword(email: String) {
        supabaseClient.auth.resetPasswordForEmail(email)
    }

    override suspend fun updatePassword(newPassword: String) {
        supabaseClient.auth.updateUser {
            password = newPassword
        }
    }

    override suspend fun signInWithProvider(provider: String) {
        supabaseClient.auth.signInWith(Google)
    }

    override fun onAuthStateChange(callback: (AuthSession?) -> Unit): () -> Unit {
        val scope = CoroutineScope(Dispatchers.Main)
        val job = scope.launch {
            supabaseClient.auth.sessionStatus.collect { status ->
                val session = getCurrentSession()
                callback(session)
            }
        }
        return { job.cancel() }
    }

    private fun UserInfo.toDomainUser(): User {
        val metadata = userMetadata
        return User(
            id = id,
            email = email ?: "",
            emailConfirmed = emailConfirmedAt != null,
            firstname = metadata?.get("firstname")?.toString()?.trim('"') ?: "",
            lastname = metadata?.get("lastname")?.toString()?.trim('"') ?: "",
            createdAt = createdAt?.epochSeconds ?: 0L,
            updatedAt = updatedAt?.epochSeconds ?: 0L
        )
    }
}
