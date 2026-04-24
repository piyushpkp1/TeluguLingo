package com.telugulingo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val titleTelugu: String,
    val titleRomanized: String,
    val description: String,
    val dayNumber: Int,
    val xpReward: Int = 20,
    val phase: Int = 1
)
