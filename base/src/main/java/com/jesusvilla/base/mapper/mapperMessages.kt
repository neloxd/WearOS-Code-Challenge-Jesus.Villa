package com.jesusvilla.base.mapper

import com.jesusvilla.base.database.entity.MessageEntity
import com.jesusvilla.base.models.Message

fun  List<Message>.mapperToEntity(): List<MessageEntity> {
    return this.map {
        MessageEntity(
            content = it.text
        )
    }
}

fun  List<MessageEntity>.mapperToModelUi(): List<Message> {
    return this.map {
        Message(
            text = it.content
        )
    }
}