package com.happyrow.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.happyrow.android.domain.model.User
import kotlin.math.absoluteValue

@Composable
fun UserAvatar(
    user: User,
    size: Int = 40,
    modifier: Modifier = Modifier
) {
    val initials = buildString {
        if (user.firstname.isNotEmpty()) append(user.firstname.first().uppercaseChar())
        if (user.lastname.isNotEmpty()) append(user.lastname.first().uppercaseChar())
    }.ifEmpty { "?" }

    val color = generateColorFromName("${user.firstname}${user.lastname}")

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size / 2.5).sp,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun generateColorFromName(name: String): Color {
    val colors = listOf(
        Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFF14B8A6),
        Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEF4444),
        Color(0xFF06B6D4), Color(0xFF10B981)
    )
    val hash = name.hashCode().absoluteValue
    return colors[hash % colors.size]
}
