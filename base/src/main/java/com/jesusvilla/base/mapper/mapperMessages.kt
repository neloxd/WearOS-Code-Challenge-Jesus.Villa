package com.jesusvilla.base.mapper

import com.jesusvilla.base.database.entity.MessageEntity
import com.jesusvilla.base.models.Message

fun  ArrayList<Message>.mapperToEntity(): ArrayList<MessageEntity> {
    return ArrayList(this.map {
        MessageEntity(
            content = it.text
        )
    })
}

fun  ArrayList<MessageEntity>.mapperToModelUi(): List<Message> {
    return this.map {
        Message(
            text = it.content
        )
    }
}

fun Message.mapperToModelUi(): MessageEntity {
    return MessageEntity(
        content = this.text
    )
}