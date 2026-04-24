package com.telugulingo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: Long,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val xpEarned: Int = 0,
    val quizScore: Int? = null
)
