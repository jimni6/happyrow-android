package com.happyrow.android.ui.events.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.menuAnchor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.happyrow.android.domain.model.ResourceCategory
import com.happyrow.android.ui.components.InputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddResourceDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: ResourceCategory, quantity: Int, suggestedQuantity: Int?) -> Unit,
    error: String? = null
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ResourceCategory.FOOD) }
    var quantity by remember { mutableStateOf("") }
    var suggestedQuantity by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une ressource") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InputField(value = name, onValueChange = { name = it }, label = "Nom")
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Catégorie") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        ResourceCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = { category = cat; categoryExpanded = false }
                            )
                        }
                    }
                }
                InputField(value = quantity, onValueChange = { quantity = it }, label = "Quantité", keyboardType = KeyboardType.Number)
                InputField(value = suggestedQuantity, onValueChange = { suggestedQuantity = it }, label = "Quantité suggérée (optionnel)", keyboardType = KeyboardType.Number)
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name.trim(), category, quantity.toIntOrNull() ?: 0, suggestedQuantity.toIntOrNull())
            }) { Text("Ajouter") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
