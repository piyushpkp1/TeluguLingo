package com.telugulingo.app.domain.repository

import com.telugulingo.app.domain.model.User
import com.telugulingo.app.domain.model.XPAndLevel
import com.telugulingo.app.domain.model.Streak
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun getUserOnce(): User?
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun addXP(amount: Int)
    suspend fun updateStreak()
    fun getXPAndLevel(): Flow<XPAndLevel>
    fun getStreak(): Flow<Streak?>
    suspend fun loseHeart()
    suspend fun refillHearts()
    suspend fun useStreakFreeze()
    suspend fun incrementLessonsCompleted()
    suspend fun saveLessonProgress(lessonId: Long, cardIndex: Int)
}
