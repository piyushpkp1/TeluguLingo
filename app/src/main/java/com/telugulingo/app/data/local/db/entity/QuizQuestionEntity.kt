package com.telugulingo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey val id: Long,
    val lessonId: Long,
    val questionTextEnglish: String,
    val correctAnswer: String,
    val correctAnswerRomanized: String,
    val optionsJson: String,
    val optionsRomanizedJson: String,
    val questionType: String,
    val hint: String? = null
)
