package com.zainal.moviedb.base

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.zainal.moviedb.R
import com.zainal.moviedb.helper.PrefsHelper
import com.zainal.moviedb.util.Constant.KEY_PREFS
import com.zainal.moviedb.util.ShimmerState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel(context: Context): ViewModel(), KoinComponent {
    private val prefsHelper by inject<PrefsHelper>()

    val shimmerState = MutableLiveData<ShimmerState>()
    fun vmShimmerState(): LiveData<ShimmerState> = shimmerState

    val errorMsg = MutableLiveData<String?>()
    fun vmErrorMsg(): LiveData<String?> = errorMsg
    fun postErrorMsg(s: String? = null) = errorMsg.postValue(s)

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        var msg = context.getString(R.string.default_error_msg)
        throwable.message?.let {
            msg = it
        }
        shimmerState.postValue(ShimmerState.STOP_VISIBLE)
        postErrorMsg(msg)
    }

    init {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
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
                                getString(KEY_PREFS)
                            )
                        }
                    }
                }

                addOnConfigUpdateListener(object : ConfigUpdateListener {
                    override fun onUpdate(configUpdate: ConfigUpdate) {
                        if (configUpdate.updatedKeys.contains(KEY_PREFS)) {
                            activate()
                        }
                    }

                    override fun onError(error: FirebaseRemoteConfigException) {
                        error.printStackTrace()
                    }
                })
            }
        }
    }
}