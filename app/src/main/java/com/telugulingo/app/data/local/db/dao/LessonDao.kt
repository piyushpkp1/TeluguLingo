package com.telugulingo.app.data.local.db.dao

import androidx.room.*
import com.telugulingo.app.data.local.db.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY dayNumber ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): LessonEntity?

    @Query("SELECT * FROM lessons WHERE dayNumber = :dayNumber")
    suspend fun getLessonByDay(dayNumber: Int): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonCount(): Int
}
