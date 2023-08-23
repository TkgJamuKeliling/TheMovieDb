package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.base.BaseRecyclerView
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.databinding.GenreItemBinding
import com.zainal.moviedb.model.response.GenresItem

class GenreAdapter(var callback : (GenresItem, GenreHolder) -> Unit): BaseRecyclerView() {

    class GenreHolder(genreItemBinding: GenreItemBinding): RecyclerView.ViewHolder(genreItemBinding.root) {
        val mcv = genreItemBinding.mcvGenre
        val acivIcon = genreItemBinding.acivIcon
        val mtvMenuTitle = genreItemBinding.mtvGenre
    }

    fun setData(list: List<GenresItem>?) = updateData(list)

    override fun onGetAdapter() = this

    override fun onGetBindHolder(
        holder: RecyclerView.ViewHolder,
        model: Equatable,
        position: Int
    ) {
        if (model is GenresItem && holder is GenreHolder) {
            callback(
                model,
                holder
            )
        }
    }

    override fun onGetViewHolder(parent: ViewGroup, viewType: Int) = GenreHolder(
        GenreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
}