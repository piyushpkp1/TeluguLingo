package com.telugulingo.app.data.mapper

import com.telugulingo.app.data.local.db.entity.*
import com.telugulingo.app.domain.model.*

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    createdAt = createdAt,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActivityDate = lastActivityDate,
    streakFreezes = streakFreezes,
    totalXP = totalXP,
    currentLevel = currentLevel,
    dailyLessonIndex = dailyLessonIndex,
    currentHearts = currentHearts,
    maxHearts = maxHearts,
    heartRefillTimestamp = heartRefillTimestamp,
    totalLessonsCompleted = totalLessonsCompleted,
    totalWordsLearned = totalWordsLearned,
    leaguePoints = leaguePoints
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    createdAt = createdAt,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastActivityDate = lastActivityDate,
    streakFreezes = streakFreezes,
    totalXP = totalXP,
    currentLevel = currentLevel,
    dailyLessonIndex = dailyLessonIndex,
    currentHearts = currentHearts,
    maxHearts = maxHearts,
    heartRefillTimestamp = heartRefillTimestamp,
    totalLessonsCompleted = totalLessonsCompleted,
    totalWordsLearned = totalWordsLearned,
    leaguePoints = leaguePoints
)

fun LessonEntity.toDomain() = Lesson(
    id = id,
    title = title,
    titleTelugu = titleTelugu,
    titleRomanized = titleRomanized,
    description = description,
    dayNumber = dayNumber,
    xpReward = xpReward,
    phase = phase,
    vocabularyCount = 0,
    quizCount = 0,
    isCompleted = false,
    completedAt = null
)

fun Lesson.toEntity() = LessonEntity(
    id = id,
    title = title,
    titleTelugu = titleTelugu,
    titleRomanized = titleRomanized,
    description = description,
    dayNumber = dayNumber,
    xpReward = xpReward,
    phase = phase
)

fun VocabularyEntity.toDomain() = Vocabulary(
    id = id,
    lessonId = lessonId,
    wordTelugu = wordTelugu,
    wordRomanized = wordRomanized,
    wordEnglish = wordEnglish,
    audioFileName = audioFileName,
    category = VocabCategory.valueOf(category),
    timesReviewed = timesReviewed,
    timesCorrect = timesCorrect,
    nextReviewDate = nextReviewDate,
    strength = strength
)

fun Vocabulary.toEntity() = VocabularyEntity(
    id = id,
    lessonId = lessonId,
    wordTelugu = wordTelugu,
    wordRomanized = wordRomanized,
    wordEnglish = wordEnglish,
    audioFileName = audioFileName,
    category = category.name,
    timesReviewed = timesReviewed,
    timesCorrect = timesCorrect,
    nextReviewDate = nextReviewDate,
    strength = strength
)
