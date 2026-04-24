package com.telugulingo.app.domain.model

data class User(
    val id: Long = 1,
    val name: String = "Learner",
    val createdAt: Long = System.currentTimeMillis(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: String? = null,
    val streakFreezes: Int = 1,
    val totalXP: Int = 0,
    val currentLevel: Int = 1,
    val dailyLessonIndex: Int = 0,
    val currentHearts: Int = 5,
    val maxHearts: Int = 5,
    val heartRefillTimestamp: Long? = null,
    val totalLessonsCompleted: Int = 0,
    val totalWordsLearned: Int = 0,
    val leaguePoints: Int = 0,
    val currentLessonId: Long = 1,
    val currentLessonCardIndex: Int = 0
)

data class XPAndLevel(
    val currentXP: Int,
    val xpForNextLevel: Int,
    val currentLevel: Int,
    val xpProgressPercent: Float
)

data class Streak(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String,
    val streakFreezesAvailable: Int,
    val streakFreezeUsedToday: Boolean
)
