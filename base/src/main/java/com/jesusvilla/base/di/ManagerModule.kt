package com.jesusvilla.base.di

import android.content.Context
import com.jesusvilla.base.database.repository.MessageRepository
import com.jesusvilla.base.managers.MessageManager
import com.jesusvilla.base.preferences.BasePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    @PreferencesQualifier
    fun provideBasePreferences(@ApplicationContext applicationContext: Context): BasePreferences {
        return BasePreferences(applicationContext)
    }

    @Provides
    @Singleton
    @ManagerQualifier
    fun provideMessageManagaer(@PreferencesQualifier pasePreference: BasePreferences,
                               @MessageRepositoryQualifier messageRepository: MessageRepository): MessageManager {
        return MessageManager(pasePreference, messageRepository)
    }
}
