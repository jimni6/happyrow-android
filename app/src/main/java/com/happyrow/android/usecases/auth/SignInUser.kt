package com.happyrow.android.usecases.auth

import com.happyrow.android.domain.model.AuthSession
import com.happyrow.android.domain.model.UserCredentials
import com.happyrow.android.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUser @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: UserCredentials): AuthSession {
        require(credentials.email.isNotBlank()) { "Email is required" }
        require(credentials.password.isNotBlank()) { "Password is required" }
        return authRepository.signIn(credentials)
    }
}
