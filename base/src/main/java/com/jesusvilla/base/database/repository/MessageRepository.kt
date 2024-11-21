package com.jesusvilla.base.database.repository

import androidx.annotation.WorkerThread
import com.jesusvilla.base.database.dao.MessageDao
import com.jesusvilla.base.database.entity.MessageEntity
import com.jesusvilla.base.database.resources.DataBaseResource
import com.jesusvilla.base.di.MessageDaoQualifier
import javax.inject.Inject

class MessageRepository @Inject constructor(
    @MessageDaoQualifier private val messageDao: MessageDao,
)  {
    @WorkerThread
    suspend fun getListMessage(): DataBaseResource<List<MessageEntity>> {
        return try {
            DataBaseResource.Success(messageDao.getMessages())
        } catch (e: Throwable) {
            DataBaseResource.Error(e)
        }
    }

    @WorkerThread
    suspend fun saveMessages(list: List<MessageEntity>): DataBaseResource<List<Long>> {
        return try {
            DataBaseResource.Success(messageDao.insertMessages(list))
        } catch (e: Throwable) {
            DataBaseResource.Error(e)
        }
    }
}