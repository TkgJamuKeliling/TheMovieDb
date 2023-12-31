package com.zainal.moviedb.helper

import com.zainal.moviedb.BuildConfig
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

    fun accessToken(): String {
        var apiKey = "${prefsUtil.getFromPrefs(
            PREFS_ACCESS_TOKEN,
            PrefsUtil.PrefsType.STRING
        )}"
        if (apiKey.isEmpty()) {
            apiKey = BuildConfig.TMDB_API_KEY
        }
        return apiKey
    }
}