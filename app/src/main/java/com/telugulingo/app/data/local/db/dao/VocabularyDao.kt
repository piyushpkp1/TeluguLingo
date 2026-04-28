package com.telugulingo.app.data.local.db.dao

import androidx.room.*
import com.telugulingo.app.data.local.db.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary WHERE lessonId = :lessonId")
    fun getVocabularyForLesson(lessonId: Long): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE lessonId = :lessonId")
    suspend fun getVocabularyForLessonOnce(lessonId: Long): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary")
    fun getAllVocabulary(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary")
    suspend fun getAllVocabularyOnce(): List<VocabularyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocabulary: VocabularyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabularyList(vocabulary: List<VocabularyEntity>)

    @Update
    suspend fun updateVocabulary(vocabulary: VocabularyEntity)
}
