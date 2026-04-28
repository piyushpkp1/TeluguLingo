package com.telugulingo.app.domain.repository

import com.telugulingo.app.domain.model.Vocabulary
import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
    fun getVocabularyForLesson(lessonId: Long): Flow<List<Vocabulary>>
    suspend fun getVocabularyForLessonOnce(lessonId: Long): List<Vocabulary>
    suspend fun getAllVocabularyOnce(): List<Vocabulary>
    suspend fun initializeVocabulary()
}
