package com.zainal.moviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.GenresItem
import com.zainal.moviedb.model.TrendingResultsItem
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState

open class MainFragmentViewModel(var repository: Repository) : BaseViewModel() {
    var isProcessGetAllData = false
    var isProcessGetTrendingData = false

    val shimmerState = MutableLiveData<ShimmerState>()
    fun vmShimmerState(): LiveData<ShimmerState> = shimmerState

    val trendingResultsItem = MutableLiveData<List<TrendingResultsItem>?>()
    fun vmTrendingResultsItem(): LiveData<List<TrendingResultsItem>?> = trendingResultsItem

    val genresItem = MutableLiveData<List<GenresItem>?>()
    fun vmGenresItem(): LiveData<List<GenresItem>?> = genresItem

    val trendingShimmerState = MutableLiveData<ShimmerState>()
    fun vmTrendingShimmerState(): LiveData<ShimmerState> = trendingShimmerState
}