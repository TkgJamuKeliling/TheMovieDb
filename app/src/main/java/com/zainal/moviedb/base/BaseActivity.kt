package com.zainal.moviedb.base

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

    fun showGlobalDialog(msg: String) {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this).apply {
            setCancelable(false)
            setMessage(msg)
            setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                closeDialog()
            }
        }
        alertDialog = materialAlertDialogBuilder.create()
        showDialog()
    }
}