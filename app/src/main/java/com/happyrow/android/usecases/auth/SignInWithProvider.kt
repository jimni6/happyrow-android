package com.happyrow.android.usecases.auth

import com.happyrow.android.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithProvider @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(provider: String = "google") {
        authRepository.signInWithProvider(provider)
    }
}
