package com.telugulingo.app.domain.model

data class Lesson(
    val id: Long,
    val title: String,
    val titleTelugu: String,
    val titleRomanized: String,
    val description: String,
    val dayNumber: Int,
    val xpReward: Int = 20,
    val phase: Int = 1,
    val vocabularyCount: Int = 0,
    val quizCount: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)
