package com.happyrow.android.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.happyrow.android.ui.auth.AuthState
import com.happyrow.android.ui.auth.AuthViewModel
import com.happyrow.android.ui.components.AppTopBar
import com.happyrow.android.ui.components.HappyRowButton
import com.happyrow.android.ui.components.UserAvatar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val user = (authState as? AuthState.Authenticated)?.user

    val memberSinceFormatter = remember {
        SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Profil",
                user = null,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    ) { padding ->
        if (user == null) {
            Text(
                text = "Non connecté",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserAvatar(user = user, size = 96)
                Text(
                    text = "${user.firstname} ${user.lastname}".trim(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (user.emailConfirmed) {
                        "Email vérifié"
                    } else {
                        "Email non vérifié"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (user.emailConfirmed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                Text(
                    text = "Membre depuis le ${memberSinceFormatter.format(Date(user.createdAt))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                HappyRowButton(
                    text = "Se déconnecter",
                    onClick = { authViewModel.signOut() },
                    isSecondary = true
                )
            }
        }
    }
}
