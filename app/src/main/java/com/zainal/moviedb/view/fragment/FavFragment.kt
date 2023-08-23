package com.zainal.moviedb.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.zainal.moviedb.databinding.FragmentFavBinding
import com.zainal.moviedb.db.MovieEntity
import com.zainal.moviedb.model.response.DetailResponse
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.view.adapter.FavAdapter
import com.zainal.moviedb.viewmodel.FavViewModel
import org.koin.android.ext.android.inject

class FavFragment : Fragment() {

    lateinit var favBinding: FragmentFavBinding
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
                //TODO
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
            favAdapter.setupData(it)
        }
    }

    private fun initView() {
        favBinding.rcvFav.apply {
            adapter = favAdapter
            setHasFixedSize(false)
            itemAnimator = null
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