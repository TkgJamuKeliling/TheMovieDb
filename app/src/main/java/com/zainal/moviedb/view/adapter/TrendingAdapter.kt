package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.base.BaseRecyclerView
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.databinding.CardItemBinding
import com.zainal.moviedb.model.TrendingResultsItem

class TrendingAdapter(var callback: (TrendingResultsItem, TrendingViewHolder) -> Unit) : BaseRecyclerView() {

    class TrendingViewHolder(cardItemBinding: CardItemBinding): RecyclerView.ViewHolder(cardItemBinding.root) {
        val root = cardItemBinding.root
        val acivCard = cardItemBinding.acivCard
        val mtvPercentageValue = cardItemBinding.containerPercentage.mtvPercentageValue
        val cpiProgress = cardItemBinding.containerPercentage.cpiProgress
        val mtvCardTitle = cardItemBinding.mtvCardTitle
        val mtvReleaseDate = cardItemBinding.mtvReleaseDate
    }

    override fun onGetAdapter() = this

    override fun onGetViewHolder(parent: ViewGroup, viewType: Int) = TrendingViewHolder(
        CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onGetBindHolder(
        holder: RecyclerView.ViewHolder,
        model: Equatable,
        position: Int
    ) {
        if (model is TrendingResultsItem && holder is TrendingViewHolder) {
            callback(
                model,
                holder
            )
        }
    }

    fun setData(trendingResultsItemList: List<TrendingResultsItem>?) = updateData(trendingResultsItemList)
}