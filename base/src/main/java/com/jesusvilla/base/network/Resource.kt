package com.jesusvilla.base.network

/**
 * Created by Jesús Villa on 19/11/24
 */
@Suppress("LongParameterList")
class Resource<T> private constructor(
    val status: Status,
    val code: String?,
    var data: T?,
    val message: String?,
    val codeHttp: Int = 0,
    val isNetworkRelated: Boolean = false,
    val title: String? = null
) {
    companion object {

        fun <T> success(
            data: T?,
            msg: String? = null,
            code: String? = null,
            codeHttp: Int = 200
        ): Resource<T> {
            return Resource(
                Status.SUCCESS,
                code,
                data,
                msg,
                codeHttp
            )
        }

        fun <T> error(
            msg: String,
            code: String? = null,
            codeHttp: Int = 999,
            isNetworkRelated: Boolean = false,
            title: String? = null
        ): Resource<T> {
            return Resource(
                Status.ERROR,
                code,
                null,
                msg,
                codeHttp,
                isNetworkRelated,
                title
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                Status.LOADING,
                null,
                data,
                null,
            )
        }
    }
}