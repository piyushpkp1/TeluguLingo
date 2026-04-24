package com.telugulingo.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.telugulingo.app.presentation.theme.XPGold

@Composable
fun XPProgressBar(
    currentXP: Int,
    xpForNextLevel: Int,
    progressPercent: Float,
    currentLevel: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent,
        animationSpec = tween(durationMillis = 800),
        label = "xp_progress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(XPGold.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$currentLevel",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = XPGold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Level $currentLevel",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "$currentXP / $xpForNextLevel XP",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(100))
                .background(Color.Black.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(XPGold, Color(0xFFFB923C))
                        )
                    )
            )
        }
    }
}
