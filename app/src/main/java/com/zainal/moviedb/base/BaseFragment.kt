package com.zainal.moviedb.base

import android.annotation.SuppressLint
import android.content.Intent
import androidx.fragment.app.Fragment
import com.zainal.moviedb.model.GenresItem
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.util.Constant.EXTRA_GENRE_DATA
import com.zainal.moviedb.view.activity.DetailActivity
import com.zainal.moviedb.view.activity.DiscoverActivity
import com.zainal.moviedb.view.adapter.GenreAdapter

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

    @SuppressLint("DiscouragedApi")
    fun genreAdapterCallback(
        genresItem: GenresItem,
        holder: GenreAdapter.GenreHolder
    ) {
        with(holder) {
            val resId = requireContext().resources.getIdentifier(
                genresItem.icon,
                "drawable",
                requireContext().packageName
            )
            acivIcon.setImageResource(resId)

            mtvMenuTitle.text = genresItem.name

            mcv.setOnClickListener {
                startActivity(Intent(
                    requireContext(),
                    DiscoverActivity::class.java
                ).apply {
                    putExtra(EXTRA_GENRE_DATA, genresItem)
                })
            }
        }
    }
}