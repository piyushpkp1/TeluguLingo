package com.telugulingo.app.presentation.screen.practice

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.telugulingo.app.presentation.theme.ErrorRed
import com.telugulingo.app.presentation.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PracticeScreen(
    lessonId: Long,
    onBack: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val micPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice Speaking") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.vocabulary.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No vocabulary to practice for this lesson.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress
                LinearProgressIndicator(
                    progress = { (uiState.currentIndex + 1).toFloat() / uiState.vocabulary.size },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Word ${uiState.currentIndex + 1} of ${uiState.vocabulary.size}",
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Phrase card
                uiState.vocabulary.getOrNull(uiState.currentIndex)?.let { vocab ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = vocab.wordTelugu,
                                style = MaterialTheme.typography.displayMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = vocab.wordRomanized,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = vocab.wordEnglish,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Listen button
                FilledTonalIconButton(
                    onClick = { viewModel.playAudio() },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Listen",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Microphone button
                if (!micPermissionState.status.isGranted) {
                    Button(onClick = { micPermissionState.launchPermissionRequest() }) {
                        Text("Grant Microphone Permission")
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (uiState.isListening) viewModel.stopListening()
                            else viewModel.startListening()
                        },
                        modifier = Modifier
                            .size(96.dp)
                            .scale(if (uiState.isListening) 1.1f else 1f)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (uiState.isListening) "Stop" else "Speak",
                            modifier = Modifier.size(64.dp),
                            tint = if (uiState.isListening) ErrorRed else MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = if (uiState.isListening) "Tap to stop" else "Tap to speak",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recognition result
                uiState.recognitionResult?.let { result ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (result.isAccurate) SuccessGreen.copy(alpha = 0.1f)
                            else ErrorRed.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "You said: \"${result.spokenText}\"",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (result.isAccurate) "Great pronunciation!" else "Try again!",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (result.isAccurate) SuccessGreen else ErrorRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousWord() },
                        enabled = uiState.currentIndex > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Previous")
                    }
                    Button(
                        onClick = { viewModel.nextWord() },
                        enabled = uiState.currentIndex < uiState.vocabulary.size - 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}
