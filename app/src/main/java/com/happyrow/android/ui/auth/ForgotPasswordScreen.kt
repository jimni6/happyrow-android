package com.happyrow.android.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.happyrow.android.ui.components.HappyRowButton
import com.happyrow.android.ui.components.InputField
import com.happyrow.android.ui.navigation.Route

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var pendingReset by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    var sawLoading by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(loginUiState.isLoading) {
        if (loginUiState.isLoading) sawLoading = true
    }

    LaunchedEffect(loginUiState.isLoading, loginUiState.error) {
        if (pendingReset && sawLoading && !loginUiState.isLoading) {
            if (loginUiState.error == null) {
                showSuccess = true
            }
            pendingReset = false
            sawLoading = false
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Reset password",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Enter your email and we'll send you a link to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            InputField(
                value = email,
                onValueChange = {
                    email = it
                    showSuccess = false
                    viewModel.clearLoginError()
                },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
            loginUiState.error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (showSuccess) {
                Text(
                    text = "If an account exists for this email, you will receive reset instructions shortly.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HappyRowButton(
                text = "Reset Password",
                onClick = {
                    pendingReset = true
                    sawLoading = false
                    showSuccess = false
                    viewModel.resetUserPassword(email.trim())
                },
                isLoading = loginUiState.isLoading,
                enabled = email.isNotBlank()
            )
            Text(
                text = "Back to login",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { navController.navigate(Route.Login.route) }
                    .padding(vertical = 4.dp)
            )
        }
    }
}
