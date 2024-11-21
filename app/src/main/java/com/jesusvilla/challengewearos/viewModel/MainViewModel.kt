package com.jesusvilla.challengewearos.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jesusvilla.base.database.resources.doError
import com.jesusvilla.base.database.resources.doSuccess
import com.jesusvilla.base.mapper.mapperToModelUi
import com.jesusvilla.base.models.DataMessage
import com.jesusvilla.base.models.MainIntent
import com.jesusvilla.base.models.Message
import com.jesusvilla.base.network.Resource
import com.jesusvilla.base.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private val TAG = "MainViewModel"
    var state by mutableStateOf(DataMessage())
        private set

    fun onEvent(event: MainIntent) {
        Log.i(TAG, "event:$event")
        when (event) {
            is MainIntent.isLoading -> {
                loadMessages()
            }
            is MainIntent.message -> {
                addNewMessage(event.str)
            }
            is MainIntent.data -> {
                state = state.copy(list = event.messages, loading = false)
            }
            else -> {

            }
            /*is HomeScreenEvents.UpdateText -> {
                updateTextField(event.q)
            }*/
        }
    }

    private fun updateTextField(str: String) {
        state = state.copy(list = ArrayList())
    }

    fun addNewMessage(str: String) {
        launcher(
            invoke = {
                Log.i(TAG, "invoke")
                Resource.success(messageManager.saveMessageDB(Message(str)))
            },
            responseResult = {
                Log.i(TAG, "responseResult")
                loadMessages()
            }
        )
    }

    private fun loadMessages() {
        Log.i(TAG, "loadMessages")
        state = state.copy(list = ArrayList(), loading = true)
        launcher(
            invoke = {
                Log.i(TAG, "invoke")
                val value = messageManager.getMessagesFromDB()
                Resource.success(value)
            },
            responseResult = { it ->
                it.doSuccess {
                    Log.i(TAG, "responseResult doSuccess ${it.size}")
                    state = state.copy(list = ArrayList(ArrayList(it).mapperToModelUi()), loading = false)
                }
                it.doError {
                    Log.i(TAG, "responseResult doError")
                    state = state.copy( loading = false)
                }
            },
            errorResponse = null
        )
    }
}