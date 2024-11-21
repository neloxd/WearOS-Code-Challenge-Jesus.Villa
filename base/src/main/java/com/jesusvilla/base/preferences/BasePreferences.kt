package com.jesusvilla.base.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BasePreferences @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun getStringSet(key: String, defaultValue: HashSet<String> = HashSet()): Set<String> =
        sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue

    fun saveStringSet(key: String, value: HashSet<String>) {
        sharedPreferences.edit().putStringSet(key, value).apply()
    }

    fun removeAll() {
        sharedPreferences.edit().clear().apply()
    }
}
