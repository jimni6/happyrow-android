package com.happyrow.android.fake

import com.happyrow.android.TestData
import com.happyrow.android.domain.model.AuthSession
import com.happyrow.android.domain.model.User
import com.happyrow.android.domain.model.UserCredentials
import com.happyrow.android.domain.model.UserRegistration
import com.happyrow.android.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    var currentUser: User? = null
    var currentSession: AuthSession? = null
    var shouldThrow: Exception? = null

    override suspend fun register(userData: UserRegistration): User {
        shouldThrow?.let { throw it }
        val user = TestData.user(email = userData.email, firstname = userData.firstname, lastname = userData.lastname)
        currentUser = user
        currentSession = TestData.authSession(user)
        return user
    }

    override suspend fun signIn(credentials: UserCredentials): AuthSession {
        shouldThrow?.let { throw it }
        val user = TestData.user(email = credentials.email)
        currentUser = user
        val session = TestData.authSession(user)
        currentSession = session
        return session
    }

    override suspend fun signOut() {
        shouldThrow?.let { throw it }
        currentUser = null
        currentSession = null
    }

    override suspend fun getCurrentUser(): User? = currentUser
    override suspend fun getCurrentSession(): AuthSession? = currentSession

    override suspend fun refreshSession(): AuthSession {
        shouldThrow?.let { throw it }
        return currentSession ?: throw IllegalStateException("No session")
    }

    override suspend fun resetPassword(email: String) {
        shouldThrow?.let { throw it }
    }

    override suspend fun updatePassword(newPassword: String) {
        shouldThrow?.let { throw it }
    }

    override suspend fun signInWithProvider(provider: String) {
        shouldThrow?.let { throw it }
    }

    override fun onAuthStateChange(callback: (AuthSession?) -> Unit): () -> Unit {
        callback(currentSession)
        return {}
    }
}
