package com.zainal.moviedb.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseFragment
import com.zainal.moviedb.callback.SwipeCallback
import com.zainal.moviedb.databinding.FragmentFavBinding
import com.zainal.moviedb.db.MovieEntity
import com.zainal.moviedb.model.response.DetailResponse
import com.zainal.moviedb.util.Constant.BASE_URL_POSTER
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.view.adapter.FavAdapter
import com.zainal.moviedb.view.adapter.FavLoadAdapter
import com.zainal.moviedb.viewmodel.FavViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FavFragment : BaseFragment() {

    private lateinit var favBinding: FragmentFavBinding
    val favViewModel by inject<FavViewModel>()

    val favAdapter = FavAdapter(::favAdapterCallback)

    private fun favAdapterCallback(
        movieEntity: MovieEntity,
        holder: FavAdapter.FavViewHolder
    ) {
        with(holder) {
            val model = Gson().fromJson(movieEntity.movieDetail, DetailResponse::class.java)
            var txtName = "-"
            var flag = true
            model.title?.let {
                if (it.isNotEmpty()) {
                    flag = false
                    txtName = it
                }
            }
            if (flag) {
                model.name?.let {
                    if (it.isNotEmpty()) {
                        txtName = it
                    }
                }
            }
            mtvName.text = txtName

            flag = true
            var txtYear = ""
            model.releaseDate?.let {
                if (it.isNotEmpty()) {
                    flag = false
                    txtYear = it.substringBefore("-")
                }
            }
            if (flag) {
                model.firstAirDate?.let {
                    if (it.isNotEmpty()) {
                        txtYear = it.substringBefore("-")
                    }
                }
            }
            mtvYear.text = getString(
                R.string.year_template,
                txtYear
            )

            Glide.with(this@FavFragment)
                .load("${BASE_URL_POSTER}${model.posterPath}")
                .override(MATCH_PARENT, 125)
                .centerCrop()
                .into(sivPoster)

            mcvFav.setOnClickListener {
                openDetailActivity(
                    model.id,
                    movieEntity.typeCategory
                )
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeView()
    }

    private fun observeView() {
        favViewModel.vmMovieEntity().observe(viewLifecycleOwner) {
            it?.let {
                lifecycleScope.launch {
                    favAdapter.submitData(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        favAdapter.refresh()
    }

    private fun initView() {
        with(favBinding) {
            rcvFav.apply {
                adapter = favAdapter.withLoadStateFooter(
                    FavLoadAdapter()
                )
                favAdapter.addLoadStateListener {
                    if (it.append.endOfPaginationReached) {
                        if (favAdapter.itemCount < 1) {
                            rcvFav.visibility = INVISIBLE
                            lottieView.visibility = VISIBLE
                        } else {
                            rcvFav.visibility = VISIBLE
                            lottieView.visibility = INVISIBLE
                        }
                    }
                }
                setHasFixedSize(false)
                itemAnimator = null

                ItemTouchHelper(object : SwipeCallback(requireContext()) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.bindingAdapterPosition
                        val movieEntity = favAdapter.snapshot()[position]

                        val detailMovie = Gson().fromJson(movieEntity?.movieDetail, DetailResponse::class.java)

                        favViewModel.removeItem(movieEntity) {
                            if (it) {
                                favAdapter.refresh()
                            }
                        }

                        val snackbar = Snackbar
                            .make(root, getString(
                                R.string.snackbar_remove_info,
                                when (movieEntity?.typeCategory) {
                                    TypeCategory.MOVIE.name.uppercase() -> detailMovie.title
                                    else -> detailMovie.name
                                }
                            ), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo_text)) {
                                favViewModel.restoreItem(movieEntity) {
                                    if (it) {
                                        favAdapter.refresh()
                                    }
                                }
                            }
                        snackbar.show()
                    }
                }).attachToRecyclerView(this)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favBinding = FragmentFavBinding.inflate(inflater, container, false)
        return favBinding.root
    }
}