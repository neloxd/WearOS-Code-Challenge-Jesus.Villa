package com.jesusvilla.base.models

sealed class MainIntent {
    data object isLoading : MainIntent()
    class hasError(error: String): MainIntent()
    class data(val messages: ArrayList<Message>): MainIntent()
    class message(val str: String): MainIntent()
}