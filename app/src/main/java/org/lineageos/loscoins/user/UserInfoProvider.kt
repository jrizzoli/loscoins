package org.lineageos.loscoins.user

import android.content.Context
import android.content.SharedPreferences

class UserInfoProvider(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, 0)

    fun isInitialized(): Boolean {
        return prefs.contains(KEY_USER_NAME)
    }

    fun setName(name: String) {
        return prefs.edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    fun setId(id: String) {
        return prefs.edit()
            .putString(KEY_USER_ID, id)
            .apply()
    }

    fun getName(): String {
        return prefs.getString(KEY_USER_NAME, null) ?: ""
    }

    fun getId(): String {
        return prefs.getString(KEY_USER_ID, null) ?: ""
    }

    private companion object {
        const val PREFS = "user_info"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_ID = "user_id"
    }
}