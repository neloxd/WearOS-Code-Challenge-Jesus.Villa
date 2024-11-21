package com.jesusvilla.base.models

data class DataMessage(
    val list: ArrayList<Message> = ArrayList(),
    val loading: Boolean = false
)
