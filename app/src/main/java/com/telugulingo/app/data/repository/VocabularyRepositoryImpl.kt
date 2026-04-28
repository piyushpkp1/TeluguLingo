package com.telugulingo.app.data.repository

import android.content.Context
import com.telugulingo.app.data.local.db.dao.VocabularyDao
import com.telugulingo.app.data.local.db.entity.VocabularyEntity
import com.telugulingo.app.data.mapper.toDomain
import com.telugulingo.app.domain.model.Vocabulary
import com.telugulingo.app.domain.repository.VocabularyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyRepositoryImpl @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    @ApplicationContext private val context: Context
) : VocabularyRepository {

    override fun getVocabularyForLesson(lessonId: Long): Flow<List<Vocabulary>> =
        vocabularyDao.getVocabularyForLesson(lessonId).map { list -> list.map { it.toDomain() } }

    override suspend fun getVocabularyForLessonOnce(lessonId: Long): List<Vocabulary> =
        vocabularyDao.getVocabularyForLessonOnce(lessonId).map { it.toDomain() }

    override suspend fun getAllVocabularyOnce(): List<Vocabulary> =
        vocabularyDao.getAllVocabularyOnce().map { it.toDomain() }

    override suspend fun getWordDictionary(): Map<String, String> {
        return try {
            val inputStream = context.assets.open("word_dictionary.json")
            val json = org.json.JSONObject(inputStream.bufferedReader().readText())
            inputStream.close()
            json.keys().asSequence().associate { key -> key to json.getString(key) }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override suspend fun initializeVocabulary() {
        val existing = vocabularyDao.getVocabularyForLessonOnce(1)
        if (existing.isNotEmpty()) return

        val json = loadVocabularyJson() ?: return
        val vocabulary = (0 until json.length()).map { index ->
            val obj = json.getJSONObject(index)
            VocabularyEntity(
                id = (index + 1).toLong(),
                lessonId = obj.getLong("lessonId"),
                wordTelugu = obj.getString("telugu"),
                wordRomanized = obj.getString("romanized"),
                wordEnglish = obj.getString("english"),
                audioFileName = null,
                category = obj.getString("category"),
                timesReviewed = 0,
                timesCorrect = 0,
                nextReviewDate = null,
                strength = 0f
            )
        }
        vocabularyDao.insertVocabularyList(vocabulary)
    }

    private fun loadVocabularyJson(): JSONArray? {
        return try {
            val inputStream = context.assets.open("vocabulary.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            JSONArray(String(buffer, Charsets.UTF_8))
        } catch (e: Exception) {
            null
        }
    }
}
