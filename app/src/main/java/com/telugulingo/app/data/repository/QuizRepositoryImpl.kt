package com.telugulingo.app.data.repository

import android.content.Context
import com.telugulingo.app.data.local.db.dao.QuizDao
import com.telugulingo.app.data.local.db.entity.QuizQuestionEntity
import com.telugulingo.app.domain.model.QuestionType
import com.telugulingo.app.domain.model.QuizQuestion
import com.telugulingo.app.domain.repository.QuizRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val quizDao: QuizDao,
    @ApplicationContext private val context: Context
) : QuizRepository {

    override fun getQuizForLesson(lessonId: Long): Flow<List<QuizQuestion>> =
        quizDao.getQuizQuestionsForLesson(lessonId).map { list -> list.map { it.toDomain() } }

    override suspend fun getQuizForLessonOnce(lessonId: Long): List<QuizQuestion> =
        quizDao.getQuizQuestionsForLessonOnce(lessonId).map { it.toDomain() }

    override suspend fun initializeQuizzes() {
        val existing = quizDao.getQuizQuestionsForLessonOnce(120)
        if (existing.isNotEmpty()) return

        val json = loadQuizJson() ?: return
        val quizzes = (0 until json.length()).map { index ->
            val obj = json.getJSONObject(index)
            val qId = (obj.getLong("lessonId") * 1000) + (index % 999) + 1L
            val optionsTel = obj.getJSONArray("optionsTelugu")
            val optionsRom = obj.getJSONArray("optionsRomanized")
            QuizQuestionEntity(
                id = qId,
                lessonId = obj.getLong("lessonId"),
                questionTextEnglish = obj.getString("questionTextEnglish"),
                correctAnswer = obj.getString("correctAnswer"),
                correctAnswerRomanized = obj.getString("correctAnswerRomanized"),
                optionsJson = optionsTel.toString(),
                optionsRomanizedJson = optionsRom.toString(),
                questionType = obj.getString("questionType"),
                hint = if (obj.isNull("hint")) null else obj.getString("hint")
            )
        }
        quizDao.insertQuizQuestions(quizzes)
    }

    private fun loadQuizJson(): JSONArray? {
        return try {
            val inputStream = context.assets.open("quizzes.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            JSONArray(String(buffer, Charsets.UTF_8))
        } catch (e: Exception) {
            null
        }
    }

    private fun QuizQuestionEntity.toDomain(): QuizQuestion {
        val optionsArray = JSONArray(optionsJson)
        val optionsRomanizedArray = JSONArray(optionsRomanizedJson)
        val optionsList = (0 until optionsArray.length()).map { optionsArray.getString(it) }
        val optionsRomanizedList = (0 until optionsRomanizedArray.length()).map { optionsRomanizedArray.getString(it) }

        return QuizQuestion(
            id = id,
            quizId = lessonId,
            questionTextEnglish = questionTextEnglish,
            correctAnswer = correctAnswer,
            correctAnswerRomanized = correctAnswerRomanized,
            options = optionsList,
            optionsRomanized = optionsRomanizedList,
            questionType = try { QuestionType.valueOf(questionType) } catch (e: Exception) { QuestionType.MULTIPLE_CHOICE },
            hint = hint
        )
    }
}
