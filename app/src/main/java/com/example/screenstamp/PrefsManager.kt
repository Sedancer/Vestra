package com.example.screenstamp

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ScreenStampPrefs", Context.MODE_PRIVATE)

    var width: Int
        get() = prefs.getInt("width", 200)
        set(value) = prefs.edit().putInt("width", value).apply()

    var height: Int
        get() = prefs.getInt("height", 200)
        set(value) = prefs.edit().putInt("height", value).apply()

    var x: Int
        get() = prefs.getInt("x", 0)
        set(value) = prefs.edit().putInt("x", value).apply()

    var y: Int
        get() = prefs.getInt("y", 0)
        set(value) = prefs.edit().putInt("y", value).apply()

    var imagePath: String?
        get() = prefs.getString("imagePath", null)
        set(value) = prefs.edit().putString("imagePath", value).apply()

    var timerDelay: Int
        get() = prefs.getInt("timerDelay", 5)
        set(value) = prefs.edit().putInt("timerDelay", value).apply()

    var hideClickCount: Int
        get() = prefs.getInt("hideClickCount", 4)
        set(value) = prefs.edit().putInt("hideClickCount", value).apply()

    var hideClickInterval: Int
        get() = prefs.getInt("hideClickInterval", 400)
        set(value) = prefs.edit().putInt("hideClickInterval", value).apply()
}
