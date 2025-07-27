package com.motgolla.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtil {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_DEPARTMENT_NAME = "department_name"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveDepartmentName(context: Context, name: String) {
        val editor = getPrefs(context).edit()
        editor.putString(KEY_DEPARTMENT_NAME, name)
        editor.apply()
    }

    fun getDepartmentName(context: Context): String? {
        return getPrefs(context).getString(KEY_DEPARTMENT_NAME, null)
    }
}
