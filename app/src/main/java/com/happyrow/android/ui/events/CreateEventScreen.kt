package com.happyrow.android.ui.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.menuAnchor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.happyrow.android.domain.model.EventCreationRequest
import com.happyrow.android.domain.model.EventType
import com.happyrow.android.ui.components.AppTopBar
import com.happyrow.android.ui.components.HappyRowButton
import com.happyrow.android.ui.components.InputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavHostController,
    eventsViewModel: EventsViewModel = hiltViewModel(),
    organizerId: String
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(EventType.PARTY) }
    var typeMenuExpanded by remember { mutableStateOf(false) }

    val createState by eventsViewModel.createEventState.collectAsStateWithLifecycle()

    LaunchedEffect(createState.success) {
        if (createState.success) {
            navController.popBackStack()
            eventsViewModel.resetCreateState()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Nouvel événement",
                user = null,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InputField(
                value = name,
                onValueChange = { name = it },
                label = "Nom"
            )
            InputField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                singleLine = false
            )
            InputField(
                value = location,
                onValueChange = { location = it },
                label = "Lieu"
            )
            InputField(
                value = dateText,
                onValueChange = { dateText = it },
                label = "Date (yyyy-MM-dd)"
            )

            ExposedDropdownMenuBox(
                expanded = typeMenuExpanded,
                onExpandedChange = { typeMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = { typeMenuExpanded = false }
                ) {
                    EventType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                selectedType = type
                                typeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            if (createState.error != null) {
                Text(
                    text = createState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HappyRowButton(
                text = "Créer l'événement",
                onClick = {
                    eventsViewModel.createNewEvent(
                        EventCreationRequest(
                            name = name.trim(),
                            description = description.trim(),
                            date = dateText.trim(),
                            location = location.trim(),
                            type = selectedType,
                            organizerId = organizerId
                        )
                    )
                },
                isLoading = createState.isLoading
            )
        }
    }
}
