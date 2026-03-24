package com.happyrow.android.ui.events.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.happyrow.android.domain.model.ParticipantStatus
import com.happyrow.android.ui.components.InputField

@Composable
fun AddParticipantDialog(
    onDismiss: () -> Unit,
    onConfirm: (email: String, status: ParticipantStatus) -> Unit,
    error: String? = null
) {
    var email by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ParticipantStatus.INVITED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un participant") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InputField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardType = KeyboardType.Email
                )
                Text("Statut initial", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(ParticipantStatus.INVITED, ParticipantStatus.CONFIRMED).forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status.name) }
                        )
                    }
                }
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(email.trim(), selectedStatus) }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
