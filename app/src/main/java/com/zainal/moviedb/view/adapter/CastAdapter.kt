package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.base.BaseRecyclerView
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.databinding.CastItemBinding
import com.zainal.moviedb.model.CastItem

class CastAdapter(var callback: (CastItem, CastItemViewHolder) -> Unit): BaseRecyclerView() {

    fun setupData(list: List<CastItem>?) = updateData(list)

    class CastItemViewHolder(castItemBinding: CastItemBinding): RecyclerView.ViewHolder(castItemBinding.root) {
        val acivAvatar = castItemBinding.acivCast
        val mtvActorName = castItemBinding.mtvActorName
        val mtvCastName = castItemBinding.mtvCastName
    }

    override fun onGetAdapter() = this

    override fun onGetBindHolder(holder: RecyclerView.ViewHolder, model: Equatable, position: Int) {
        if (model is CastItem && holder is CastItemViewHolder) {
            callback(
                model,
                holder
            )
        }
    }

    override fun onGetViewHolder(parent: ViewGroup, viewType: Int) = CastItemViewHolder(
        CastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
}