package com.zainal.moviedb.base

import android.content.Intent
import androidx.fragment.app.Fragment
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.view.activity.DetailActivity

open class BaseFragment: Fragment() {

    fun openDetailActivity(id: Int, name: String) = startActivity(
        Intent(
            requireContext(),
            DetailActivity::class.java
        ).apply {
            putExtra(Constant.EXTRA_ID, id)
            putExtra(Constant.EXTRA_CATEGORY, name)
        }
    )
}