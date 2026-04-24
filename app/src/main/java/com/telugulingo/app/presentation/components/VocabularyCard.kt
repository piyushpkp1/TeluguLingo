package com.telugulingo.app.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telugulingo.app.domain.model.Vocabulary
import com.telugulingo.app.presentation.theme.XPGold

@Composable
fun VocabularyCard(
    vocabulary: Vocabulary,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "card_flip"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { isFlipped = !isFlipped }
    ) {
        if (rotation <= 90f) {
            CardFront(vocabulary = vocabulary)
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardBack(vocabulary = vocabulary, onPlayAudio = onPlayAudio)
            }
        }
    }
}

@Composable
private fun CardFront(vocabulary: Vocabulary) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = vocabulary.wordRomanized,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 52.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                shape = RoundedCornerShape(100),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text(
                    text = "Tap to reveal",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CardBack(vocabulary: Vocabulary, onPlayAudio: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface,
                        )
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = vocabulary.wordTelugu,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = vocabulary.wordRomanized,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = vocabulary.wordEnglish,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                FilledTonalIconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Play pronunciation",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(XPGold.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+5 XP",
                        style = MaterialTheme.typography.labelMedium,
                        color = XPGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
