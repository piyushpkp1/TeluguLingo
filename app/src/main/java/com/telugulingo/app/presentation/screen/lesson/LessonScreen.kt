package com.telugulingo.app.presentation.screen.lesson

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.telugulingo.app.presentation.components.VocabularyCard
import com.telugulingo.app.presentation.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: Long,
    onBack: () -> Unit,
    onQuizClick: () -> Unit,
    onPracticeClick: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LessonTopBar(
                currentIndex = uiState.currentCardIndex,
                totalCount = uiState.vocabulary.size,
                onBack = {
                    if (!hasNavigated) {
                        hasNavigated = true
                        onBack()
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Vocabulary card with cross-fade transition between cards
                AnimatedContent(
                    targetState = uiState.currentCardIndex,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        val direction = if (targetState > initialState) 1 else -1
                        (slideInHorizontally(
                            initialOffsetX = { it / 3 * direction },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { -it / 3 * direction },
                                animationSpec = tween(300)
                            ) + fadeOut(animationSpec = tween(300))
                        )
                    },
                    label = "card_transition"
                ) { cardIndex ->
                    uiState.vocabulary.getOrNull(cardIndex)?.let { vocab ->
                        VocabularyCard(
                            vocabulary = vocab,
                            onPlayAudio = { viewModel.playAudio() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousCard() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = uiState.currentCardIndex > 0,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Previous", fontWeight = FontWeight.SemiBold)
                    }

                    val isLastCard = uiState.currentCardIndex >= uiState.vocabulary.size - 1
                    Button(
                        onClick = {
                            viewModel.markCardViewed()
                            if (isLastCard) {
                                if (!hasNavigated) {
                                    hasNavigated = true
                                    viewModel.completeLesson()
                                    onBack()
                                }
                            } else {
                                viewModel.nextCard()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = if (isLastCard) {
                            ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        } else ButtonDefaults.buttonColors()
                    ) {
                        Text(
                            if (isLastCard) "Finish" else "Next",
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            if (isLastCard) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Completion panel — slides in when all cards viewed
                AnimatedVisibility(
                    visible = uiState.allCardsViewed,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    CompletionPanel(
                        onPracticeClick = {
                            if (!hasNavigated) {
                                hasNavigated = true
                                viewModel.completeLesson()
                                onPracticeClick()
                            }
                        },
                        onQuizClick = {
                            if (!hasNavigated) {
                                hasNavigated = true
                                viewModel.completeLesson()
                                onQuizClick()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonTopBar(
    currentIndex: Int,
    totalCount: Int,
    onBack: () -> Unit,
) {
    val progress = if (totalCount == 0) 0f else (currentIndex + 1).toFloat() / totalCount
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(400),
        label = "lesson_progress"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (totalCount > 0) "${currentIndex + 1} / $totalCount" else "",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun CompletionPanel(
    onPracticeClick: () -> Unit,
    onQuizClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lesson Complete!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onPracticeClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Practice")
                }
                Button(
                    onClick = onQuizClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Quiz")
                }
            }
        }
    }
}
