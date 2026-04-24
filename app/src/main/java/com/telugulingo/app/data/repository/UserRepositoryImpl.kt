package com.telugulingo.app.data.repository

import com.telugulingo.app.data.local.db.dao.UserDao
import com.telugulingo.app.data.local.db.entity.UserEntity
import com.telugulingo.app.data.mapper.toDomain
import com.telugulingo.app.data.mapper.toEntity
import com.telugulingo.app.domain.model.Streak
import com.telugulingo.app.domain.model.User
import com.telugulingo.app.domain.model.XPAndLevel
import com.telugulingo.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    private val levelThresholds = listOf(
        0, 100, 250, 500, 1000, 1750, 2750, 4000, 5500, 7500,
        10000, 13000, 17000, 22000, 28000, 35000, 43000, 52000, 62000, 75000,
        90000, 110000, 135000, 165000, 200000
    )

    override fun getUser(): Flow<User?> = userDao.getUser().map { it?.toDomain() }

    override suspend fun getUserOnce(): User? = userDao.getUserOnce()?.toDomain()

    override suspend fun createUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun addXP(amount: Int) {
        val user = userDao.getUserOnce() ?: return
        val newTotal = user.totalXP + amount
        val newLevel = calculateLevel(newTotal)
        userDao.updateUser(
            user.copy(
                totalXP = newTotal,
                currentLevel = newLevel,
                leaguePoints = user.leaguePoints + amount
            )
        )
    }

    override suspend fun updateStreak() {
        val user = userDao.getUserOnce() ?: return
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        val lastDate = user.lastActivityDate

        val newStreak = when {
            lastDate == null -> 1
            isConsecutiveDay(lastDate, today) -> user.currentStreak + 1
            else -> 1
        }

        val newLongest = maxOf(newStreak, user.longestStreak)
        userDao.updateUser(
            user.copy(
                currentStreak = newStreak,
                longestStreak = newLongest,
                lastActivityDate = today,
                currentHearts = user.maxHearts
            )
        )
    }

    override suspend fun loseHeart() {
        val user = userDao.getUserOnce() ?: return
        userDao.updateUser(
            user.copy(
                currentHearts = (user.currentHearts - 1).coerceAtLeast(0),
                heartRefillTimestamp = if (user.currentHearts <= 1) System.currentTimeMillis() + 30 * 60 * 1000 else user.heartRefillTimestamp
            )
        )
    }

    override suspend fun refillHearts() {
        val user = userDao.getUserOnce() ?: return
        userDao.updateUser(
            user.copy(
                currentHearts = user.maxHearts,
                heartRefillTimestamp = null
            )
        )
    }

    override suspend fun useStreakFreeze() {
        val user = userDao.getUserOnce() ?: return
        if (user.streakFreezes > 0) {
            userDao.updateUser(user.copy(streakFreezes = user.streakFreezes - 1))
        }
    }

    override suspend fun incrementLessonsCompleted() {
        val user = userDao.getUserOnce() ?: return
        userDao.updateUser(user.copy(totalLessonsCompleted = user.totalLessonsCompleted + 1))
    }

    override suspend fun saveLessonProgress(lessonId: Long, cardIndex: Int) {
        val user = userDao.getUserOnce() ?: return
        userDao.updateUser(
            user.copy(
                currentLessonId = lessonId,
                currentLessonCardIndex = cardIndex
            )
        )
    }

    override fun getXPAndLevel(): Flow<XPAndLevel> = userDao.getUser().map { user ->
        val totalXP = user?.totalXP ?: 0
        val level = calculateLevel(totalXP)
        val currentThreshold = levelThresholds.getOrElse(level - 1) { 0 }
        val nextThreshold = levelThresholds.getOrElse(level) { currentThreshold + 7500 }
        val xpInLevel = totalXP - currentThreshold
        val xpNeeded = nextThreshold - currentThreshold
        val progress = if (xpNeeded > 0) xpInLevel.toFloat() / xpNeeded else 1f
        XPAndLevel(totalXP, nextThreshold, level, progress.coerceIn(0f, 1f))
    }

    override fun getStreak(): Flow<Streak?> = userDao.getUser().map { user ->
        user?.let {
            Streak(
                currentStreak = it.currentStreak,
                longestStreak = it.longestStreak,
                lastActivityDate = it.lastActivityDate ?: "",
                streakFreezesAvailable = it.streakFreezes,
                streakFreezeUsedToday = false
            )
        }
    }

    private fun calculateLevel(totalXP: Int): Int {
        for (i in levelThresholds.indices.reversed()) {
            if (totalXP >= levelThresholds[i]) return i + 1
        }
        return 1
    }

    private fun isConsecutiveDay(last: String, current: String): Boolean {
        return try {
            val lastDate = LocalDate.parse(last)
            val currentDate = LocalDate.parse(current)
            ChronoUnit.DAYS.between(lastDate, currentDate) == 1L
        } catch (e: Exception) {
            false
        }
    }
}
