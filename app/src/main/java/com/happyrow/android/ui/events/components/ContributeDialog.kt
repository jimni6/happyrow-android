package com.happyrow.android.ui.events.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.happyrow.android.domain.model.Resource
import com.happyrow.android.ui.components.InputField

@Composable
fun ContributeDialog(
    resource: Resource,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int) -> Unit
) {
    var quantity by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contribuer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(resource.name, style = MaterialTheme.typography.titleMedium)
                val progressText = if (resource.suggestedQuantity != null) {
                    "${resource.currentQuantity}/${resource.suggestedQuantity} apportés"
                } else {
                    "${resource.currentQuantity} apportés"
                }
                Text(progressText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                InputField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "Quantité à contribuer",
                    keyboardType = KeyboardType.Number
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(quantity.toIntOrNull() ?: 1) }) {
                Text("Contribuer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
