package com.jesusvilla.base.di

import android.content.Context
import androidx.room.Room
import com.jesusvilla.base.constants.DataBaseConfig
import com.jesusvilla.base.database.DataBase
import com.jesusvilla.base.database.dao.MessageDao
import com.jesusvilla.base.database.repository.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DataBase {
        return Room.databaseBuilder(
            context,
            DataBase::class.java,
            DataBaseConfig.dataBaseName
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    @MessageDaoQualifier
    fun provideMessageDao(dataBase: DataBase) = dataBase.messageDao()

    @Provides
    @Singleton
    @MessageRepositoryQualifier
    fun provideMessageRepository(
        @MessageDaoQualifier messageDao: MessageDao,
    ): MessageRepository {
        return MessageRepository(
            messageDao
        )
    }
}