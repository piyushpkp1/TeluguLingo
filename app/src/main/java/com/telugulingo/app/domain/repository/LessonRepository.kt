package com.telugulingo.app.domain.repository

import com.telugulingo.app.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getAllLessons(): Flow<List<Lesson>>
    suspend fun getLessonById(id: Long): Lesson?
    suspend fun getLessonByDay(dayNumber: Int): Lesson?
    suspend fun initializeLessons()
}
