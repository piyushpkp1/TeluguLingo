package com.telugulingo.app.data.local.db.dao

import androidx.room.*
import com.telugulingo.app.data.local.db.entity.QuizQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_questions WHERE lessonId = :lessonId")
    fun getQuizQuestionsForLesson(lessonId: Long): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quiz_questions WHERE lessonId = :lessonId")
    suspend fun getQuizQuestionsForLessonOnce(lessonId: Long): List<QuizQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(question: QuizQuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestions(questions: List<QuizQuestionEntity>)
}
