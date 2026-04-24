package com.telugulingo.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.telugulingo.app.presentation.theme.ErrorRed
import com.telugulingo.app.presentation.theme.SuccessGreen

@Composable
fun QuizOption(
    option: String,
    optionRomanized: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        isCorrect == true -> SuccessGreen.copy(alpha = 0.15f)
        isCorrect == false && isSelected -> ErrorRed.copy(alpha = 0.15f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isCorrect == true -> SuccessGreen
        isCorrect == false && isSelected -> ErrorRed
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    OutlinedCard(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = optionRomanized,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isCorrect != null) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    tint = if (isCorrect) SuccessGreen else ErrorRed
                )
            }
        }
    }
}
