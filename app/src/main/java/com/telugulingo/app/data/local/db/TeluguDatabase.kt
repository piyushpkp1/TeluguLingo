package com.telugulingo.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.telugulingo.app.data.local.db.dao.*
import com.telugulingo.app.data.local.db.entity.*

@Database(
    entities = [
        UserEntity::class,
        LessonEntity::class,
        VocabularyEntity::class,
        QuizQuestionEntity::class,
        LessonProgressEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class TeluguDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun quizDao(): QuizDao
    abstract fun progressDao(): ProgressDao
}
