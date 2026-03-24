package com.happyrow.android.ui.events.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.happyrow.android.domain.model.Resource

@Composable
fun ResourceList(
    resources: List<Resource>,
    onEdit: (Resource) -> Unit,
    onDelete: (Resource) -> Unit,
    onContribute: (Resource) -> Unit,
    modifier: Modifier = Modifier
) {
    if (resources.isEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aucune ressource",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            resources.forEach { resource ->
                ResourceItem(
                    resource = resource,
                    onEdit = { onEdit(resource) },
                    onDelete = { onDelete(resource) },
                    onContribute = { onContribute(resource) }
                )
            }
        }
    }
}
