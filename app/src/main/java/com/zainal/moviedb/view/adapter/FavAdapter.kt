package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.base.BaseRecyclerView
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.databinding.FavItemBinding
import com.zainal.moviedb.db.MovieEntity

class FavAdapter(var callback: (MovieEntity, FavViewHolder) -> Unit): BaseRecyclerView() {
    class FavViewHolder(favItemBinding: FavItemBinding): RecyclerView.ViewHolder(favItemBinding.root) {
        val mcvFav = favItemBinding.root
        val sivPoster = favItemBinding.sivPoster
        val mtvName = favItemBinding.mtvName
    }

    fun setupData(list: List<MovieEntity>?) = updateData(list)

    override fun onGetAdapter() = this

    override fun onGetBindHolder(holder: RecyclerView.ViewHolder, model: Equatable, position: Int) {
        if (model is MovieEntity && holder is FavViewHolder) {
            callback(
                model,
                holder
            )
        }
    }

    override fun onGetViewHolder(parent: ViewGroup, viewType: Int) = FavViewHolder(
        FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
}