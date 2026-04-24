package com.telugulingo.app.data.local.db.dao

import androidx.room.*
import com.telugulingo.app.data.local.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1")
    suspend fun getUserOnce(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}
