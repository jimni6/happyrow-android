package com.happyrow.android.usecases.auth

import com.happyrow.android.domain.model.User
import com.happyrow.android.domain.model.UserRegistration
import com.happyrow.android.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(registration: UserRegistration): User {
        validate(registration)
        return authRepository.register(registration)
    }

    private fun validate(data: UserRegistration) {
        require(data.email.isNotBlank()) { "Email is required" }
        require(EMAIL_REGEX.matches(data.email)) { "Invalid email format" }
        require(data.password.length >= 8) { "Password must be at least 8 characters" }
        require(data.firstname.length >= 2) { "First name must be at least 2 characters" }
        require(data.lastname.length >= 2) { "Last name must be at least 2 characters" }
        if (data.confirmPassword != null) {
            require(data.password == data.confirmPassword) { "Passwords do not match" }
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
