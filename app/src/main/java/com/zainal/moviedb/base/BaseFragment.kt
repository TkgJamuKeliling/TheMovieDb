package com.zainal.moviedb.base

import android.content.Intent
import androidx.fragment.app.Fragment
import com.zainal.moviedb.util.Constant.EXTRA_CATEGORY
import com.zainal.moviedb.util.Constant.EXTRA_ID
import com.zainal.moviedb.view.activity.DetailActivity

open class BaseFragment: Fragment() {

    fun openDetailActivity(id: Int, name: String) = startActivity(
        Intent(
            requireContext(),
            DetailActivity::class.java
        ).apply {
            putExtra(EXTRA_ID, id)
            putExtra(EXTRA_CATEGORY, name)
        }
    )
}