package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.base.BaseRecyclerView
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.databinding.DiscoverItemBinding
import com.zainal.moviedb.model.DiscoverResultsItem

class DiscoverAdapter(
    var callback: (
        DiscoverResultsItem,
        DiscoverViewHolder ) -> Unit
): BaseRecyclerView() {

    fun setupData(list: List<DiscoverResultsItem>?) = updateData(list)

    class DiscoverViewHolder(discoverItemBinding: DiscoverItemBinding): RecyclerView.ViewHolder(discoverItemBinding.root) {
        val sivPoster = discoverItemBinding.sivPoster
    }

    override fun onGetAdapter() = this

    override fun onGetBindHolder(holder: RecyclerView.ViewHolder, model: Equatable, position: Int) {
        if (model is DiscoverResultsItem && holder is DiscoverViewHolder) {
            callback(
                model,
                holder
            )
        }
    }

    override fun onGetViewHolder(parent: ViewGroup, viewType: Int) = DiscoverViewHolder(
        DiscoverItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
}