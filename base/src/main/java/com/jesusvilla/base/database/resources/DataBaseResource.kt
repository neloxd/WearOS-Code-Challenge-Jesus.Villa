package com.jesusvilla.base.database.resources

sealed class DataBaseResource<out T> {
    class Success<out T>(val data: T) : DataBaseResource<T>()
    class Error<out T>(val throwable: Throwable) : DataBaseResource<T>()
}

inline fun <reified T> DataBaseResource<T>.doSuccess(callback: (data: T) -> Unit) {
    if (this is DataBaseResource.Success) {
        callback(data)
    }
}

inline fun <reified T> DataBaseResource<T>.doError(callback: (throwable: Throwable) -> Unit) {
    if (this is DataBaseResource.Error) {
        callback(throwable)
    }
}
