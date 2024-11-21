package com.jesusvilla.base.viewModel

import android.content.Context
import android.widget.Toast
import androidx.annotation.CheckResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesusvilla.base.di.ManagerQualifier
import com.jesusvilla.base.managers.MessageManager
import com.jesusvilla.base.models.MainIntent
import com.jesusvilla.base.network.Resource
import com.jesusvilla.base.network.Status
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Jesús Villa on 20/11/24
 */
abstract class BaseViewModel : ViewModel() {

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    @Inject
    @ManagerQualifier
    lateinit var messageManager: MessageManager


    companion object {
        private const val TAG = ""
    }

    //region livedata

    protected val _mainIntent = MutableLiveData<MainIntent>()
    val mainIntent: LiveData<MainIntent> get() = _mainIntent

    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun notifyError(errorMsg: String) {
        Toast.makeText(appContext, errorMsg, Toast.LENGTH_LONG).show()
    }

    /**
     * The launcher method execute viewModelsScope,
     * and then processing the method
     *
     * @param invoke: It is suspend function
     * @param responseResult: Return de result from invoke proccess
     * @param errorResponse: Return de error from invoke proccess
     *
     */
    protected fun <T> launcher(
        invoke: suspend () -> Resource<T>,
        responseResult: ((T) -> Unit)? = null,
        errorResponse: ((Pair<String, Boolean>) -> Unit)? = null,
        showMessageError: Boolean? = false,
        showLoading: Boolean = true
    ) {

        viewModelScope.launch {
            if (showLoading) _isLoading.postValue(true)
            val response = invoke()
            if (showLoading) _isLoading.postValue(false)
            if (response.status == Status.SUCCESS) {
                responseResult?.invoke(response.data!!)
            } else {
                response.message?.let { error ->
                    errorResponse?.let { callBackError ->
                        showMessageError?.let {
                            if (it) {
                                notifyError(response.message.toString())
                            }
                        }
                        callBackError(Pair(error, response.isNetworkRelated))
                    } ?: notifyError(error)
                }
            }
        }
    }

    @CheckResult
    fun onIsLoading(): LiveData<Boolean> = isLoading
}
