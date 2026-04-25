package com.telugulingo.app.data.local.db.dao

import androidx.room.*
import com.telugulingo.app.data.local.db.entity.LessonProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    suspend fun getProgressForLesson(lessonId: Long): LessonProgressEntity?

    @Query("SELECT * FROM lesson_progress")
    fun getAllProgress(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LessonProgressEntity)

    @Update
    suspend fun updateProgress(progress: LessonProgressEntity)

    @Query("SELECT COUNT(*) FROM lesson_progress WHERE isCompleted = 1")
    suspend fun getCompletedLessonCount(): Int

    @Query("DELETE FROM lesson_progress")
    suspend fun deleteAllProgress()
}
