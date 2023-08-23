package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.base.BaseRecyclerView
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.databinding.TrailerItemBinding
import com.zainal.moviedb.model.response.VideoResultsItem

class TrailerAdapter(var callback: (VideoResultsItem, TrailerItemViewHolder) -> Unit): BaseRecyclerView() {

    fun setupData(list: List<VideoResultsItem>?) = updateData(list)

    class TrailerItemViewHolder(trailerItemBinding: TrailerItemBinding): RecyclerView.ViewHolder(trailerItemBinding.root) {
        val ytPlayer = trailerItemBinding.ytPlayerView
        val viewTransparent = trailerItemBinding.viewtransparent
    }

    override fun onGetAdapter() = this

    override fun onGetBindHolder(holder: RecyclerView.ViewHolder, model: Equatable, position: Int) {
        if (model is VideoResultsItem && holder is TrailerItemViewHolder) {
            callback(
                model,
                holder
            )
        }
    }

    override fun onGetViewHolder(parent: ViewGroup, viewType: Int) = TrailerItemViewHolder(
        TrailerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
}