package com.jesusvilla.base.managers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesusvilla.base.database.repository.MessageRepository
import com.jesusvilla.base.di.MessageRepositoryQualifier
import com.jesusvilla.base.di.PreferencesQualifier
import com.jesusvilla.base.models.Message
import com.jesusvilla.base.preferences.BasePreferences
import javax.inject.Inject


class MessageManager @Inject constructor(
    @PreferencesQualifier private val preferences: BasePreferences,
    @MessageRepositoryQualifier private val messageRepository: MessageRepository
) {

    companion object {
        const val MESSAGES_KEY = "MESSAGES_KEY"
    }

    fun saveMessagePreferences(data: Message) {
        val json = Gson().toJson(getMessagesFromPreferences().add(data))
        preferences.saveString(MESSAGES_KEY, json)
    }

    fun getMessagesFromPreferences(): ArrayList<Message> {
        val json = preferences.getString(MESSAGES_KEY)
        return if(json.isEmpty()) {
            ArrayList()
        } else {
            val listType = object : TypeToken<ArrayList<Message>>() {}.type
            Gson().fromJson(json,listType)
        }
    }
}