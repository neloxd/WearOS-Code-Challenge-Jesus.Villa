package com.jesusvilla.base.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "messages", indices = [Index(value = ["idEntity"], unique = true)])
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val idEntity: Long = 0,
    val content: String
)