package com.happyrow.android.usecases.auth

import com.happyrow.android.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPassword @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String) {
        require(email.isNotBlank()) { "Email is required" }
        require(EMAIL_REGEX.matches(email)) { "Invalid email format" }
        authRepository.resetPassword(email)
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
