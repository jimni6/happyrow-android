package com.happyrow.android.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.happyrow.android.ui.auth.AuthState
import com.happyrow.android.ui.auth.AuthViewModel
import com.happyrow.android.ui.components.AppTopBar
import com.happyrow.android.ui.components.ErrorMessage
import com.happyrow.android.ui.components.LoadingScreen
import com.happyrow.android.ui.components.UserAvatar
import com.happyrow.android.ui.events.EventsUiState
import com.happyrow.android.ui.events.EventsViewModel
import com.happyrow.android.ui.events.components.EventCard
import com.happyrow.android.ui.navigation.Route
import java.util.Calendar

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    eventsViewModel: EventsViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val eventsState by eventsViewModel.eventsState.collectAsStateWithLifecycle()

    val authenticatedUser = (authState as? AuthState.Authenticated)?.user

    LaunchedEffect(authenticatedUser?.id) {
        val id = authenticatedUser?.id ?: return@LaunchedEffect
        eventsViewModel.loadEvents(id)
    }

    val greeting = currentGreeting()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "HappyRow",
                user = authenticatedUser,
                onSignOut = { authViewModel.signOut() },
                onNavigateBack = null
            )
        },
        floatingActionButton = {
            val organizerId = authenticatedUser?.id
            if (organizerId != null) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Route.CreateEvent.createRoute(organizerId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Créer un événement"
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (authenticatedUser != null) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserAvatar(user = authenticatedUser, size = 56)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = authenticatedUser.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (authenticatedUser.emailConfirmed) {
                                "Email vérifié"
                            } else {
                                "Email non vérifié"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (authenticatedUser.emailConfirmed) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            when (val state = eventsState) {
                is EventsUiState.Loading -> LoadingScreen(message = "Chargement des événements…")
                is EventsUiState.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = {
                        authenticatedUser?.id?.let { eventsViewModel.loadEvents(it) }
                    }
                )
                is EventsUiState.Success -> {
                    if (state.events.isEmpty()) {
                        EmptyEventsState(modifier = Modifier.fillMaxSize())
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.events, key = { it.id }) { event ->
                                EventCard(
                                    event = event,
                                    onClick = {
                                        navController.navigate(Route.EventDetail.createRoute(event.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun currentGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Bonjour"
        hour < 18 -> "Bon après-midi"
        else -> "Bonsoir"
    }
}

@Composable
private fun EmptyEventsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aucun événement",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Créez votre premier événement avec le bouton +.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
