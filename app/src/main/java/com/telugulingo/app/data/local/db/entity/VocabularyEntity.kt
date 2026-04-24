package com.telugulingo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey val id: Long,
    val lessonId: Long,
    val wordTelugu: String,
    val wordRomanized: String,
    val wordEnglish: String,
    val audioFileName: String? = null,
    val category: String,
    val timesReviewed: Int = 0,
    val timesCorrect: Int = 0,
    val nextReviewDate: Long? = null,
    val strength: Float = 0f
)
