package com.telugulingo.app.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.telugulingo.app.presentation.components.StreakBadge
import com.telugulingo.app.presentation.components.XPProgressBar
import com.telugulingo.app.presentation.theme.BrandPrimary
import com.telugulingo.app.presentation.theme.LevelPurple
import com.telugulingo.app.presentation.theme.SuccessGreen
import com.telugulingo.app.presentation.theme.XPGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLessonClick: (Long) -> Unit,
    onProgressClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TeluguLingo",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = onProgressClick) {
                        Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Progress")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Hero header with gradient
                item {
                    HeroHeader(
                        streakCount = uiState.streak?.currentStreak ?: 0,
                        currentXP = uiState.xpAndLevel.currentXP,
                        xpForNextLevel = uiState.xpAndLevel.xpForNextLevel,
                        currentLevel = uiState.xpAndLevel.currentLevel,
                        progressPercent = uiState.xpAndLevel.xpProgressPercent,
                    )
                }

                // Today's Lesson
                uiState.todaysLesson?.let { lesson ->
                    item {
                        SectionLabel(text = "Today's Lesson")
                    }
                    item {
                        TodaysLessonCard(
                            title = lesson.title,
                            titleTelugu = lesson.titleTelugu,
                            description = lesson.description,
                            onClick = { onLessonClick(lesson.id) }
                        )
                    }
                }

                // Quick Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            value = "${uiState.lessonsCompleted}",
                            label = "Lessons",
                            accent = BrandPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = "${uiState.wordsLearned}",
                            label = "Words",
                            accent = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            value = "${uiState.user?.dailyLessonIndex?.plus(1) ?: 1}",
                            label = "Day",
                            accent = LevelPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item { SectionLabel(text = "All Lessons") }

                items(uiState.lessons) { lesson ->
                    val currentIndex = uiState.user?.dailyLessonIndex ?: 0
                    val state = when {
                        lesson.dayNumber <= currentIndex -> LessonState.COMPLETED
                        lesson.dayNumber == currentIndex + 1 -> LessonState.ACTIVE
                        else -> LessonState.LOCKED
                    }
                    LessonListItem(
                        lesson = lesson,
                        state = state,
                        onClick = { onLessonClick(lesson.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun HeroHeader(
    streakCount: Int,
    currentXP: Int,
    xpForNextLevel: Int,
    currentLevel: Int,
    progressPercent: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        BrandPrimary,
                        Color(0xFF8B5CF6),
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Let's learn some Telugu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                StreakBadge(streakCount = streakCount)
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Inline the XP bar with its own styling for the hero
            HeroXPBar(
                currentXP = currentXP,
                xpForNextLevel = xpForNextLevel,
                currentLevel = currentLevel,
                progressPercent = progressPercent,
            )
        }
    }
}

@Composable
private fun HeroXPBar(
    currentXP: Int,
    xpForNextLevel: Int,
    currentLevel: Int,
    progressPercent: Float,
) {
    Column {
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
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$currentLevel",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Level $currentLevel",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            Text(
                text = "$currentXP / $xpForNextLevel XP",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(100))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressPercent.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100))
                    .background(
                        Brush.horizontalGradient(listOf(XPGold, Color(0xFFFBBF24)))
                    )
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
    )
}

@Composable
private fun TodaysLessonCard(
    title: String,
    titleTelugu: String,
    description: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = titleTelugu,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private enum class LessonState { LOCKED, ACTIVE, COMPLETED }

@Composable
private fun LessonListItem(
    lesson: com.telugulingo.app.domain.model.Lesson,
    state: LessonState,
    onClick: () -> Unit,
) {
    val (iconBg, iconTint) = when (state) {
        LessonState.COMPLETED -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        LessonState.ACTIVE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        LessonState.LOCKED -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    LessonState.COMPLETED -> Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                    LessonState.ACTIVE -> Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                    LessonState.LOCKED -> Text(
                        text = "${lesson.dayNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = iconTint
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Day ${lesson.dayNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = lesson.titleRomanized,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${lesson.xpReward}",
                    style = MaterialTheme.typography.labelMedium,
                    color = XPGold,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = XPGold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
