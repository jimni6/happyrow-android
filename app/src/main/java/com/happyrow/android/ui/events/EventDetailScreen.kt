package com.happyrow.android.ui.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.happyrow.android.domain.model.ContributionCreationRequest
import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCreationRequest
import com.happyrow.android.domain.model.ResourceUpdateRequest
import com.happyrow.android.ui.auth.AuthState
import com.happyrow.android.ui.auth.AuthViewModel
import com.happyrow.android.ui.components.AppTopBar
import com.happyrow.android.ui.components.ErrorMessage
import com.happyrow.android.ui.components.HappyRowButton
import com.happyrow.android.ui.components.LoadingScreen
import com.happyrow.android.ui.events.components.AddParticipantDialog
import com.happyrow.android.ui.events.components.AddResourceDialog
import com.happyrow.android.ui.events.components.ContributeDialog
import com.happyrow.android.ui.events.components.EditResourceDialog
import com.happyrow.android.ui.events.components.ParticipantList
import com.happyrow.android.ui.events.components.ResourceList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventDetailScreen(
    navController: NavHostController,
    eventDetailViewModel: EventDetailViewModel = hiltViewModel(),
    participantsViewModel: ParticipantsViewModel = hiltViewModel(),
    resourcesViewModel: ResourcesViewModel = hiltViewModel(),
    contributionsViewModel: ContributionsViewModel = hiltViewModel()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by eventDetailViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val participantsState by participantsViewModel.uiState.collectAsStateWithLifecycle()
    val resourcesState by resourcesViewModel.uiState.collectAsStateWithLifecycle()
    val addParticipantError by participantsViewModel.addError.collectAsStateWithLifecycle()
    val addResourceError by resourcesViewModel.addError.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddParticipantDialog by remember { mutableStateOf(false) }
    var showAddResourceDialog by remember { mutableStateOf(false) }
    var editingResource by remember { mutableStateOf<Resource?>(null) }
    var contributingResource by remember { mutableStateOf<Resource?>(null) }

    val userId = (authState as? AuthState.Authenticated)?.user?.id

    LaunchedEffect(uiState) {
        if (uiState is EventDetailUiState.Deleted) {
            navController.popBackStack()
        }
        if (uiState is EventDetailUiState.Success) {
            val eventId = (uiState as EventDetailUiState.Success).event.id
            participantsViewModel.loadParticipants(eventId)
            resourcesViewModel.loadResources(eventId)
        }
    }

    val dateFormatter = remember { SimpleDateFormat("d MMMM yyyy", Locale.FRENCH) }

    Scaffold(
        topBar = { AppTopBar(title = "Détail", user = null, onNavigateBack = { navController.navigateUp() }) }
    ) { padding ->
        when (val state = uiState) {
            is EventDetailUiState.Loading -> LoadingScreen(message = "Chargement…", modifier = Modifier.fillMaxSize().padding(padding))
            is EventDetailUiState.Error -> ErrorMessage(message = state.message, onRetry = { eventDetailViewModel.loadEvent() }, modifier = Modifier.fillMaxSize().padding(padding))
            is EventDetailUiState.Deleted -> {}
            is EventDetailUiState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(state.event.name, style = MaterialTheme.typography.headlineSmall)
                    Text(dateFormatter.format(Date(state.event.date)), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(state.event.location, style = MaterialTheme.typography.bodyLarge)
                    Text("Type : ${state.event.type.name}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(state.event.description, style = MaterialTheme.typography.bodyLarge)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Participants section
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        val pCount = (participantsState as? ParticipantsUiState.Success)?.participants?.size ?: 0
                        Text("Participants ($pCount)", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { showAddParticipantDialog = true }) { Text("+ Ajouter") }
                    }
                    when (val pState = participantsState) {
                        is ParticipantsUiState.Loading -> LoadingScreen(message = "Chargement…")
                        is ParticipantsUiState.Error -> ErrorMessage(message = pState.message, onRetry = { participantsViewModel.loadParticipants(state.event.id) })
                        is ParticipantsUiState.Success -> ParticipantList(
                            participants = pState.participants,
                            onUpdateStatus = { email, status -> participantsViewModel.updateStatus(state.event.id, email, status) },
                            onRemove = { email -> participantsViewModel.remove(state.event.id, email) }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Resources section
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        val rCount = (resourcesState as? ResourcesUiState.Success)?.resources?.size ?: 0
                        Text("Ressources ($rCount)", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { showAddResourceDialog = true }) { Text("+ Ajouter") }
                    }
                    when (val rState = resourcesState) {
                        is ResourcesUiState.Loading -> LoadingScreen(message = "Chargement…")
                        is ResourcesUiState.Error -> ErrorMessage(message = rState.message, onRetry = { resourcesViewModel.loadResources(state.event.id) })
                        is ResourcesUiState.Success -> ResourceList(
                            resources = rState.resources,
                            onEdit = { editingResource = it },
                            onDelete = { resourcesViewModel.removeResource(it.id) },
                            onContribute = { contributingResource = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    if (userId != null) {
                        HappyRowButton(text = "Supprimer l'événement", onClick = { showDeleteDialog = true }, isSecondary = true)
                    }
                }
            }
        }
    }

    if (showDeleteDialog && userId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer l'événement ?") },
            text = { Text("Cette action est irréversible.") },
            confirmButton = { TextButton(onClick = { showDeleteDialog = false; eventDetailViewModel.delete(userId) }) { Text("Supprimer", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") } }
        )
    }

    if (showAddParticipantDialog) {
        val eventId = (uiState as? EventDetailUiState.Success)?.event?.id ?: ""
        AddParticipantDialog(
            onDismiss = { showAddParticipantDialog = false; participantsViewModel.clearAddError() },
            onConfirm = { email, status -> participantsViewModel.addNewParticipant(eventId, email, status); showAddParticipantDialog = false },
            error = addParticipantError
        )
    }

    if (showAddResourceDialog) {
        val eventId = (uiState as? EventDetailUiState.Success)?.event?.id ?: ""
        AddResourceDialog(
            onDismiss = { showAddResourceDialog = false; resourcesViewModel.clearAddError() },
            onConfirm = { name, category, qty, sugQty ->
                resourcesViewModel.createNewResource(ResourceCreationRequest(eventId, name, category, qty, sugQty))
                showAddResourceDialog = false
            },
            error = addResourceError
        )
    }

    editingResource?.let { resource ->
        EditResourceDialog(
            resource = resource,
            onDismiss = { editingResource = null },
            onConfirm = { name, category, qty, sugQty ->
                resourcesViewModel.updateExistingResource(resource.id, ResourceUpdateRequest(name, category, qty, sugQty))
                editingResource = null
            }
        )
    }

    contributingResource?.let { resource ->
        val eventId = (uiState as? EventDetailUiState.Success)?.event?.id ?: ""
        ContributeDialog(
            resource = resource,
            onDismiss = { contributingResource = null },
            onConfirm = { qty ->
                contributionsViewModel.contribute(
                    ContributionCreationRequest(eventId, resource.id, userId ?: "", qty),
                    onSuccess = { resourcesViewModel.loadResources(eventId) }
                )
                contributingResource = null
            }
        )
    }
}
