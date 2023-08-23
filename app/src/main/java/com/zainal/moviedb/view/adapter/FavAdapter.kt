package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.databinding.FavItemBinding
import com.zainal.moviedb.db.MovieEntity

class FavAdapter(var callback: (MovieEntity, FavViewHolder) -> Unit): PagingDataAdapter<MovieEntity, FavAdapter.FavViewHolder>(
    object : DiffUtil.ItemCallback<MovieEntity>() {
        override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.movieId == newItem.movieId
                    && oldItem.movieDetail == newItem.movieDetail
                    && oldItem.typeCategory == newItem.typeCategory
        }

        override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean {
            return oldItem == newItem
        }
    }
) {
    class FavViewHolder(favItemBinding: FavItemBinding): RecyclerView.ViewHolder(favItemBinding.root) {
        val mcvFav = favItemBinding.root
        val sivPoster = favItemBinding.sivPoster
        val mtvName = favItemBinding.mtvName
        val mtvYear = favItemBinding.mtvYear
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        callback(
            getItem(position)!!,
            holder
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavViewHolder(
        FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
}