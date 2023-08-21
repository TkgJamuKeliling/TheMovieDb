package com.zainal.moviedb.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.zainal.moviedb.util.Constant.PREFS_FILE_NAME

class PrefsUtil(val context: Context) {
    enum class PrefsType {
        STRING,
        INT,
        BOOLEAN,
        FLOAT,
        LONG,
        STRINGSET
    }

    private val masterKeyBuilder = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKeyBuilder,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getFromPrefs(mKey: String, type: PrefsType): Any {
        return with(sharedPreferences) {
            when (type) {
                PrefsType.STRING -> getString(mKey, "") ?:""
                PrefsType.INT -> getInt(mKey, 0)
                PrefsType.BOOLEAN -> getBoolean(mKey, false)
                PrefsType.LONG -> getLong(mKey, 0L)
                PrefsType.FLOAT -> getFloat(mKey, 0f)
                else -> getStringSet(mKey, emptySet()) ?: emptySet<String>()
            }
        }
    }

    fun saveToPrefs(mKey: String, mValue: Any) {
        with(sharedPreferences.edit()) {
            when (mValue) {
                is String -> putString(mKey, "$mValue")
                is Int -> putInt(mKey, mValue)
                is Float -> putFloat(mKey, mValue)
                is Boolean -> putBoolean(mKey, mValue)
                is Long -> putLong(mKey, mValue)
                else -> putStringSet(mKey, mutableSetOf("$mValue"))
            }
            apply()
        }
    }
}