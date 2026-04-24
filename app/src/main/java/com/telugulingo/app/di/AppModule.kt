package com.telugulingo.app.di

import android.content.Context
import androidx.room.Room
import com.telugulingo.app.data.local.db.TeluguDatabase
import com.telugulingo.app.data.local.db.dao.*
import com.telugulingo.app.data.repository.*
import com.telugulingo.app.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TeluguDatabase {
        return Room.databaseBuilder(
            context,
            TeluguDatabase::class.java,
            "telugu_lingo_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(database: TeluguDatabase): UserDao = database.userDao()

    @Provides
    fun provideLessonDao(database: TeluguDatabase): LessonDao = database.lessonDao()

    @Provides
    fun provideVocabularyDao(database: TeluguDatabase): VocabularyDao = database.vocabularyDao()

    @Provides
    fun provideQuizDao(database: TeluguDatabase): QuizDao = database.quizDao()

    @Provides
    fun provideProgressDao(database: TeluguDatabase): ProgressDao = database.progressDao()

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepositoryImpl(userDao)

    @Provides
    @Singleton
    fun provideLessonRepository(lessonDao: LessonDao): LessonRepository = LessonRepositoryImpl(lessonDao)

    @Provides
    @Singleton
    fun provideVocabularyRepository(vocabularyDao: VocabularyDao, @ApplicationContext context: Context): VocabularyRepository = VocabularyRepositoryImpl(vocabularyDao, context)

    @Provides
    @Singleton
    fun provideQuizRepository(quizDao: QuizDao, @ApplicationContext context: Context): QuizRepository = QuizRepositoryImpl(quizDao, context)
}
