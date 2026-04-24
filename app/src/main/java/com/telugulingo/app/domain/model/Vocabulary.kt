package com.telugulingo.app.domain.model

data class Vocabulary(
    val id: Long,
    val lessonId: Long,
    val wordTelugu: String,
    val wordRomanized: String,
    val wordEnglish: String,
    val audioFileName: String? = null,
    val category: VocabCategory = VocabCategory.COMMON_PHRASE,
    val timesReviewed: Int = 0,
    val timesCorrect: Int = 0,
    val nextReviewDate: Long? = null,
    val strength: Float = 0f
)

enum class VocabCategory {
    GREETING,
    NUMBER,
    PRONOUN,
    VERB,
    ADJECTIVE,
    FAMILY,
    FOOD,
    DIRECTION,
    TIME,
    EMOTION,
    COMMON_PHRASE,
    ANIMAL,
    BODY,
    CLOTHING,
    COLOR,
    PLACE,
    WEATHER,
    SCHOOL,
    WORK,
    TRANSPORT,
    NATURE
}
