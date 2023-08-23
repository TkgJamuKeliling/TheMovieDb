package com.zainal.moviedb.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.zainal.moviedb.util.Constant
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
            model.originalTitle?.let {
                if (it.isNotEmpty()) {
                    flag = false
                    txtName = it
                }
            }
            if (flag) {
                model.originalName?.let {
                    if (it.isNotEmpty()) {
                        txtName = it
                    }
                }
            }
            mtvName.text = txtName

            Glide.with(this@FavFragment)
                .load("${Constant.BASE_URL_POSTER}${model.posterPath}")
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
                                    TypeCategory.MOVIE.name.uppercase() -> detailMovie.originalTitle
                                    else -> detailMovie.originalName
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