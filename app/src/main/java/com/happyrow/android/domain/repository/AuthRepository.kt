package com.happyrow.android.domain.repository

import com.happyrow.android.domain.model.AuthSession
import com.happyrow.android.domain.model.User
import com.happyrow.android.domain.model.UserCredentials
import com.happyrow.android.domain.model.UserRegistration

interface AuthRepository {
    suspend fun register(userData: UserRegistration): User
    suspend fun signIn(credentials: UserCredentials): AuthSession
    suspend fun signOut()
    suspend fun getCurrentUser(): User?
    suspend fun getCurrentSession(): AuthSession?
    suspend fun refreshSession(): AuthSession
    suspend fun resetPassword(email: String)
    suspend fun updatePassword(newPassword: String)
    suspend fun signInWithProvider(provider: String)
    fun onAuthStateChange(callback: (AuthSession?) -> Unit): () -> Unit
}
