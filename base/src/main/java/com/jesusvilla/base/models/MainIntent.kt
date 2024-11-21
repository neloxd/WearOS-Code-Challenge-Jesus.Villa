package com.jesusvilla.base.models

sealed class MainIntent {
    data object isLoading : MainIntent()
    class hasError(error: String): MainIntent()
    class data(messages: ArrayList<Message>): MainIntent()
}