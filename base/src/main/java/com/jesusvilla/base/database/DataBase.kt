package com.jesusvilla.base.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jesusvilla.base.database.dao.MessageDao
import com.jesusvilla.base.database.entity.MessageEntity

@Database(
    entities = [
        MessageEntity::class
    ],
    version = 1, exportSchema = false
)
abstract class DataBase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}