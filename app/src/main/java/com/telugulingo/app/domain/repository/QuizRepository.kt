package com.telugulingo.app.domain.repository

import com.telugulingo.app.domain.model.Quiz
import com.telugulingo.app.domain.model.QuizQuestion
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuizForLesson(lessonId: Long): Flow<List<QuizQuestion>>
    suspend fun getQuizForLessonOnce(lessonId: Long): List<QuizQuestion>
    suspend fun initializeQuizzes()
}
