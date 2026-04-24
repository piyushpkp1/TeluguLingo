package com.telugulingo.app.presentation.screen.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.telugulingo.app.domain.model.QuestionType
import com.telugulingo.app.domain.model.QuizQuestion
import com.telugulingo.app.presentation.components.QuizOption
import com.telugulingo.app.presentation.theme.ErrorRed
import com.telugulingo.app.presentation.theme.XPGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    lessonId: Long,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Hearts display
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(uiState.maxHearts) { index ->
                            Icon(
                                imageVector = if (index < uiState.currentHearts)
                                    Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (index < uiState.currentHearts)
                                    ErrorRed else Color.Gray.copy(alpha = 0.4f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.isComplete -> {
                QuizCompleteScreen(
                    totalQuestions = uiState.questions.size,
                    correctAnswers = uiState.correctAnswers,
                    xpEarned = uiState.xpEarned,
                    onDone = onComplete
                )
            }
            uiState.questions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No quiz available for this lesson yet.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onComplete) {
                            Text("Go Back")
                        }
                    }
                }
            }
            uiState.heartsDepleted -> {
                HeartsDepletedScreen(
                    onWait = onBack,
                    onUseFreeze = { viewModel.useStreakFreeze() },
                    hasFreeze = uiState.hasStreakFreeze
                )
            }
            else -> {
                QuizContent(
                    uiState = uiState,
                    onOptionSelected = viewModel::selectOption,
                    onFillBlankAnswer = viewModel::setFillBlankAnswer,
                    onSubmit = viewModel::submitAnswer,
                    onNext = viewModel::nextQuestion,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun HeartsDepletedScreen(
    onWait: () -> Unit,
    onUseFreeze: () -> Unit,
    hasFreeze: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💔",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Out of Hearts!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Wait 30 minutes for a heart to refill, or use a streak freeze.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (hasFreeze) {
            OutlinedButton(onClick = onUseFreeze) {
                Text("Use Streak Freeze")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(onClick = onWait) {
            Text("Go Back")
        }
    }
}

@Composable
private fun QuizContent(
    uiState: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onFillBlankAnswer: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion = uiState.questions[uiState.currentQuestionIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress + Hearts row
        LinearProgressIndicator(
            progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.questions.size },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "+${uiState.xpPerQuestion} XP",
                style = MaterialTheme.typography.labelMedium,
                color = XPGold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Question
        Text(
            text = currentQuestion.questionTextEnglish,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question-type specific UI
        when (currentQuestion.questionType) {
            QuestionType.FILL_IN_BLANK -> {
                FillInBlankQuestion(
                    question = currentQuestion,
                    answer = uiState.fillBlankAnswer,
                    isRevealed = uiState.isAnswerRevealed,
                    onAnswerChange = onFillBlankAnswer
                )
            }
            QuestionType.TRANSLATE_TELUGU_TO_EN -> {
                // Show Telugu word prominently
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentQuestion.correctAnswer,
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        currentQuestion.hint?.let { hint ->
                            Text(
                                text = "(Hint: $hint)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Show options for translate
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(currentQuestion.options) { index, option ->
                        val isCorrect = if (uiState.isAnswerRevealed) {
                            option == currentQuestion.correctAnswer
                        } else null

                        QuizOption(
                            option = option,
                            optionRomanized = currentQuestion.optionsRomanized.getOrNull(index) ?: "",
                            isSelected = uiState.selectedOptionIndex == index,
                            isCorrect = if (uiState.isAnswerRevealed) option == currentQuestion.correctAnswer else null,
                            isEnabled = !uiState.isAnswerRevealed,
                            onClick = { onOptionSelected(index) }
                        )
                    }
                }
            }
            QuestionType.TRANSLATE_EN_TO_TELUGU -> {
                // Show English meaning prominently
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentQuestion.correctAnswer,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(currentQuestion.options) { index, option ->
                        QuizOption(
                            option = option,
                            optionRomanized = currentQuestion.optionsRomanized.getOrNull(index) ?: "",
                            isSelected = uiState.selectedOptionIndex == index,
                            isCorrect = if (uiState.isAnswerRevealed) option == currentQuestion.correctAnswer else null,
                            isEnabled = !uiState.isAnswerRevealed,
                            onClick = { onOptionSelected(index) }
                        )
                    }
                }
            }
            else -> {
                // MULTIPLE_CHOICE - original layout
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(currentQuestion.options) { index, option ->
                        QuizOption(
                            option = option,
                            optionRomanized = currentQuestion.optionsRomanized.getOrNull(index) ?: "",
                            isSelected = uiState.selectedOptionIndex == index,
                            isCorrect = if (uiState.isAnswerRevealed) option == currentQuestion.correctAnswer else null,
                            isEnabled = !uiState.isAnswerRevealed,
                            onClick = { onOptionSelected(index) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action button
        val canSubmit = when (currentQuestion.questionType) {
            QuestionType.FILL_IN_BLANK -> uiState.fillBlankAnswer.isNotBlank()
            else -> uiState.selectedOptionIndex != null
        }

        Button(
            onClick = { if (uiState.isAnswerRevealed) onNext() else onSubmit() },
            enabled = canSubmit || uiState.isAnswerRevealed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when {
                    uiState.isAnswerRevealed && uiState.currentQuestionIndex < uiState.questions.size - 1 -> "Next Question"
                    uiState.isAnswerRevealed -> "Finish Quiz"
                    else -> "Submit Answer"
                }
            )
        }
    }
}

@Composable
private fun FillInBlankQuestion(
    question: QuizQuestion,
    answer: String,
    isRevealed: Boolean,
    onAnswerChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            enabled = !isRevealed,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Type your answer") },
            placeholder = { Text(question.hint ?: "Enter Telugu word in romanized text") },
            singleLine = true,
            isError = isRevealed && answer != question.correctAnswer,
            supportingText = if (isRevealed) {
                { Text("Correct: ${question.correctAnswer} (${question.correctAnswerRomanized})") }
            } else null
        )

        if (isRevealed) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (answer == question.correctAnswer)
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else ErrorRed.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (answer == question.correctAnswer) "✓ Correct!" else "✗ Incorrect",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (answer == question.correctAnswer)
                            Color(0xFF4CAF50) else ErrorRed
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizCompleteScreen(
    totalQuestions: Int,
    correctAnswers: Int,
    xpEarned: Int,
    onDone: () -> Unit
) {
    val percentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
    val passed = percentage >= 70

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (passed) "🎉" else "💪",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (passed) "Amazing!" else "Keep Practicing!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$correctAnswers out of $totalQuestions correct",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.displayLarge,
            color = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = XPGold.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+$xpEarned XP",
                    style = MaterialTheme.typography.titleLarge,
                    color = XPGold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}
