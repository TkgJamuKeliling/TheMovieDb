package com.zainal.moviedb.base

import androidx.fragment.app.Fragment
import com.zainal.moviedb.helper.PrefsHelper
import org.koin.android.ext.android.inject

open class BaseFragment: Fragment() {
    val prefsHelper by inject<PrefsHelper>()
}