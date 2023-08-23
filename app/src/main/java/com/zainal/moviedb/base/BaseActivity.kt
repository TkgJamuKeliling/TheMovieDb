package com.zainal.moviedb.base

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {
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