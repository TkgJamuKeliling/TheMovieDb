package com.zainal.moviedb.base

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zainal.moviedb.helper.PrefsHelper
import org.koin.android.ext.android.inject

open class BaseActivity: AppCompatActivity() {
    val prefsHelper by inject<PrefsHelper>()
    var alertDialog: AlertDialog? = null

    fun showDialog() {
        if (!isDestroyed && !isFinishing) {
            alertDialog?.show()
        }
    }

    fun closeDialog() {
        if (!isDestroyed && !isFinishing) {
            alertDialog?.dismiss()
        }
        alertDialog = null
    }
}