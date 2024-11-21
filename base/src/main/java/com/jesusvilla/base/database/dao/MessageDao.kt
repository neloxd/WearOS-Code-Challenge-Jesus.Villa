package com.jesusvilla.base.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jesusvilla.base.database.entity.MessageEntity

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(messages: List<MessageEntity>): List<Long>

    @Query("DELETE FROM MESSAGES")
    suspend fun removeAll()

    @Transaction
    @Query("SELECT * FROM MESSAGES ORDER BY idEntity")
    suspend fun getMessages():  List<MessageEntity>
}