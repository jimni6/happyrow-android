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
import com.happyrow.android.domain.model.UserRegistration
import com.happyrow.android.ui.components.HappyRowButton
import com.happyrow.android.ui.components.InputField
import com.happyrow.android.ui.navigation.Route

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val registerUiState by viewModel.registerUiState.collectAsStateWithLifecycle()
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()

    var firstname by rememberSaveable { mutableStateOf("") }
    var lastname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var confirmError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(registerUiState.registrationSuccess) {
        if (registerUiState.registrationSuccess) {
            navController.navigate(Route.Login.route) {
                popUpTo(Route.Register.route) { inclusive = true }
            }
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
                text = "Create account",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            InputField(
                value = firstname,
                onValueChange = {
                    firstname = it
                    viewModel.clearRegisterError()
                },
                label = "First name"
            )
            InputField(
                value = lastname,
                onValueChange = {
                    lastname = it
                    viewModel.clearRegisterError()
                },
                label = "Last name"
            )
            InputField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearRegisterError()
                },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
            InputField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearRegisterError()
                },
                label = "Password",
                isPassword = true
            )
            InputField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmError = null
                    viewModel.clearRegisterError()
                },
                label = "Confirm password",
                isPassword = true,
                error = confirmError
            )
            registerUiState.error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            loginUiState.error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            HappyRowButton(
                text = "Create Account",
                onClick = {
                    confirmError = null
                    if (password != confirmPassword) {
                        confirmError = "Passwords do not match"
                        return@HappyRowButton
                    }
                    viewModel.register(
                        UserRegistration(
                            email = email.trim(),
                            password = password,
                            firstname = firstname.trim(),
                            lastname = lastname.trim(),
                            confirmPassword = confirmPassword
                        )
                    )
                },
                isLoading = registerUiState.isLoading
            )
            HappyRowButton(
                text = "Sign up with Google",
                onClick = { viewModel.signInWithGoogle() },
                isLoading = loginUiState.isLoading,
                isSecondary = true
            )
            Text(
                text = "Already have an account? Sign in",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { navController.navigate(Route.Login.route) }
                    .padding(vertical = 4.dp)
            )
        }
    }
}
