package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.databinding.BottomLoadingBinding

class FavLoadAdapter: LoadStateAdapter<FavLoadAdapter.LoadingViewHolder>() {
    inner class LoadingViewHolder(var bottomLoadingBinding: BottomLoadingBinding): RecyclerView.ViewHolder(bottomLoadingBinding.root)

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bottomLoadingBinding.root.visibility = when (loadState) {
            LoadState.Loading -> VISIBLE
            else -> INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = LoadingViewHolder(
        BottomLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
}