package com.telugulingo.app.domain.model

data class Quiz(
    val id: Long,
    val lessonId: Long,
    val questions: List<QuizQuestion>,
    val totalXP: Int = 30,
    val passingScore: Int = 70
)

data class QuizQuestion(
    val id: Long,
    val quizId: Long,
    val questionTextEnglish: String,
    val correctAnswer: String,
    val correctAnswerRomanized: String,
    val options: List<String>,
    val optionsRomanized: List<String>,
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val hint: String? = null
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRANSLATE_TELUGU_TO_EN,
    TRANSLATE_EN_TO_TELUGU,
    FILL_IN_BLANK
}
