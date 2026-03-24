package com.happyrow.android.usecases.auth

import com.happyrow.android.domain.model.User
import com.happyrow.android.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUser @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}
