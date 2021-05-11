package nstu.avt716.etroshkova.diplom.data.preferences

import android.content.SharedPreferences

open class BasePreferences(
    protected val preferences: SharedPreferences
) {

    fun SharedPreferences.readBoolean(key: String, default: Boolean = false) =
        getBoolean(key, default)

    fun SharedPreferences.saveBoolean(key: String, value: Boolean) =
        save { it.putBoolean(key, value) }

    fun SharedPreferences.readLong(key: String, default: Long = 0) =
        getLong(key, default)

    fun SharedPreferences.saveLong(key: String, value: Long) =
        save { it.putLong(key, value) }

    fun SharedPreferences.readString(key: String, default: String = "") =
        getString(key, default) ?: default

    fun SharedPreferences.saveString(key: String, value: String) =
        save { it.putString(key, value) }

    fun SharedPreferences.clearString(key: String, default: String = "") =
        saveString(key, default)

    private fun SharedPreferences.save(action: (SharedPreferences.Editor) -> Unit) {
        edit().also {
            action.invoke(it)
            it.apply()
        }
    }
}
