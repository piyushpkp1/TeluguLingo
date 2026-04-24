package com.telugulingo.app.domain.model

data class UserProgress(
    val lessonId: Long,
    val lessonCompleted: Boolean,
    val vocabularyLearned: Int,
    val quizScore: Int?,
    val xpEarned: Int,
    val completedAt: Long?
)
