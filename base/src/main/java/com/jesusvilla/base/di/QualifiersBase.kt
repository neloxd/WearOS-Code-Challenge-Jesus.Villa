package com.jesusvilla.base.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferencesQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ManagerQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageDaoQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageRepositoryQualifier