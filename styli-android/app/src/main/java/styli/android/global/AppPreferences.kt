package styli.android.global

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private lateinit var sharedPreferences: SharedPreferences

    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences("styli.sharedprefs", MODE_PRIVATE)
    }

    var jwt: String?
        get() = Key.JWT.getString()
        set(value) = Key.JWT.setString(value)

    var username: String?
        get() = Key.USERNAME.getString()
        set(value) = Key.USERNAME.setString(value)

    private enum class Key {
        JWT, USERNAME;

        fun getString(): String? = if (sharedPreferences.contains(name)) sharedPreferences.getString(name, "") else null
        fun setString(value: String?) = value?.let { sharedPreferences.edit { putString(name, value) } } ?: remove()

        fun exists(): Boolean = sharedPreferences.contains(name)
        fun remove() = sharedPreferences.edit { remove(name) }
    }
}