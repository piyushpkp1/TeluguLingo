package com.telugulingo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 1,
    val name: String,
    val createdAt: Long,
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
