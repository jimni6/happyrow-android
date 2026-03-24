package com.happyrow.android.usecases.auth

import com.happyrow.android.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUser @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}
