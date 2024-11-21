package com.jesusvilla.base.managers

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesusvilla.base.database.entity.MessageEntity
import com.jesusvilla.base.database.repository.MessageRepository
import com.jesusvilla.base.database.resources.DataBaseResource
import com.jesusvilla.base.database.resources.doError
import com.jesusvilla.base.database.resources.doSuccess
import com.jesusvilla.base.di.MessageRepositoryQualifier
import com.jesusvilla.base.di.PreferencesQualifier
import com.jesusvilla.base.mapper.mapperToEntity
import com.jesusvilla.base.mapper.mapperToModelUi
import com.jesusvilla.base.models.Message
import com.jesusvilla.base.network.Resource
import com.jesusvilla.base.network.Status
import com.jesusvilla.base.preferences.BasePreferences
import javax.inject.Inject


class MessageManager @Inject constructor(
    @PreferencesQualifier private val preferences: BasePreferences,
    @MessageRepositoryQualifier private val messageRepository: MessageRepository
) {

    companion object {
        const val MESSAGES_KEY = "MESSAGES_KEY"
        const val TAG = "MessageManager"
    }

    fun saveMessagePreferences(data: Message) {
        val value = getMessagesFromPreferences()
        if(value.status == Status.SUCCESS) {
            val json = Gson().toJson(value.data!!.add(data))
            preferences.saveString(MESSAGES_KEY, json)
        }
    }

    fun getMessagesFromPreferences(): Resource<ArrayList<Message>> {
        val json = preferences.getString(MESSAGES_KEY)
        return if(json.isEmpty()) {
            Resource.success(ArrayList())
        } else {
            val listType = object : TypeToken<ArrayList<Message>>() {}.type
            Resource.success(Gson().fromJson(json,listType))
        }
    }

    suspend fun saveMessageDB(data: Message) {
        val value = getMessagesFromDB()
        value.doSuccess { list ->
            Log.i(TAG, "saveMessageDB getMessagesFromDB doSuccess: ${list.size}")
            val message = messageRepository.removeMessage()
            message.doSuccess {
                Log.i(TAG, "removeMessage doSuccess")
                val valid = ArrayList(list)
                valid.add(data.mapperToModelUi())
                val result = messageRepository.saveMessages(valid.toList())
                result.doSuccess {
                    Log.i(TAG, "saveMessages doSuccess ")
                    return@doSuccess
                }
                result.doError {
                    Log.i(TAG, "saveMessages doError -> $it ")
                    return@doError
                }
            }
            message.doError {
                Log.i(TAG, "removeMessage doError :$it")
                val valid = ArrayList<MessageEntity>()
                valid.add(data.mapperToModelUi())
               val result = messageRepository.saveMessages(valid.toList())
                result.doSuccess {

                }
                result.doError {

                }
            }

        }
        value.doError {
            Log.i(TAG, "saveMessageDB throw: $it")
            messageRepository.removeMessage()
            val valid = ArrayList<MessageEntity>()
            valid.add(data.mapperToModelUi())
            messageRepository.saveMessages(valid.toList())
        }


    }

    suspend fun getMessagesFromDB(): DataBaseResource<List<MessageEntity>> {
        return messageRepository.getListMessage()
    }
}