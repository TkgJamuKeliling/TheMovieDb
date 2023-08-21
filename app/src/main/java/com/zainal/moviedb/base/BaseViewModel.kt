package com.zainal.moviedb.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.zainal.moviedb.helper.PrefsHelper
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.util.TrendingSeason
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel: ViewModel(), KoinComponent {
    val prefsHelper by inject<PrefsHelper>()

    var isGenreRequestProcess = false
    var isTrendingRequestProcess = false

    init {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600
                }
            )

            fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModelScope.launch {
                        prefsHelper.saveKeyPrefs(
                            getString(Constant.KEY_PREFS)
                        )

                        fetchAllData()
                    }
                }
            }

            addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    if (configUpdate.updatedKeys.contains(Constant.KEY_PREFS)) {
                        activate()
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    error.printStackTrace()
                }
            })
        }
    }

    fun clearRequestState() {
        isGenreRequestProcess = false
        isTrendingRequestProcess = false
    }

    open fun fetchAllData(trendingSeason: TrendingSeason = TrendingSeason.DAY) {}
}