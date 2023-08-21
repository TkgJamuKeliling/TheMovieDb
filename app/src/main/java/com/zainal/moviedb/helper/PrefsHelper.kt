package com.zainal.moviedb.helper

import com.zainal.moviedb.util.Constant.PREFS_ACCESS_TOKEN
import com.zainal.moviedb.util.Constant.PREFS_API_KEY
import com.zainal.moviedb.util.PrefsUtil
import org.json.JSONObject

class PrefsHelper(private val prefsUtil: PrefsUtil) {

    fun saveKeyPrefs(s: String) {
        val jsonObject = JSONObject(s)
        arrayOf(
            PREFS_API_KEY,
            PREFS_ACCESS_TOKEN
        ).forEach {
            prefsUtil.saveToPrefs(it, jsonObject.getString(it) ?:"")
        }
    }

    fun getStringPrefs(s: String) = "${prefsUtil.getFromPrefs(
        s,
        PrefsUtil.PrefsType.STRING
    )}"

    fun apiKey() = getStringPrefs(PREFS_API_KEY)

    fun accessToken() = getStringPrefs(PREFS_ACCESS_TOKEN)
}