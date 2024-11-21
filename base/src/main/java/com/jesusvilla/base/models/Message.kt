package com.jesusvilla.base.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    var text: String
) : Parcelable
