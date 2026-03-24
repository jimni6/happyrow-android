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
import com.happyrow.android.domain.model.Resource
import com.happyrow.android.domain.model.ResourceCategory
import com.happyrow.android.ui.components.InputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditResourceDialog(
    resource: Resource,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: ResourceCategory, quantity: Int, suggestedQuantity: Int?) -> Unit
) {
    var name by remember { mutableStateOf(resource.name) }
    var category by remember { mutableStateOf(resource.category) }
    var quantity by remember { mutableStateOf(resource.currentQuantity.toString()) }
    var suggestedQuantity by remember { mutableStateOf(resource.suggestedQuantity?.toString() ?: "") }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la ressource") },
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
                            DropdownMenuItem(text = { Text(cat.name) }, onClick = { category = cat; categoryExpanded = false })
                        }
                    }
                }
                InputField(value = quantity, onValueChange = { quantity = it }, label = "Quantité", keyboardType = KeyboardType.Number)
                InputField(value = suggestedQuantity, onValueChange = { suggestedQuantity = it }, label = "Quantité suggérée", keyboardType = KeyboardType.Number)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name.trim(), category, quantity.toIntOrNull() ?: 0, suggestedQuantity.toIntOrNull())
            }) { Text("Enregistrer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
