package com.zainal.moviedb.viewmodel

import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TrendingSeason
import com.zainal.moviedb.util.TypeCategory
import kotlinx.coroutines.launch

class TvShowViewModel(repository: Repository) : MainFragmentViewModel(repository) {
    override fun fetchAllData(trendingSeason: TrendingSeason) {
        super.fetchAllData(trendingSeason)
        if (!isProcessGetAllData && !isProcessGetTrendingData) {
            isProcessGetAllData = true
            shimmerState.postValue(ShimmerState.START)

            viewModelScope.launch {
                repository.fetchMainFragmentData(
                    trendingSeason,
                    TypeCategory.TV
                ) { mTrendingResultsItem, mGenresItem ->
                    shimmerState.postValue(ShimmerState.STOP_GONE)

                    trendingResultsItem.postValue(mTrendingResultsItem)
                    genresItem.postValue(mGenresItem)

                    isProcessGetAllData = false
                }
            }
        }
    }

    fun getTvTrendingData(trendingSeason: TrendingSeason) {
        if (!isProcessGetTrendingData) {
            isProcessGetTrendingData = true

            trendingResultsItem.postValue(null)
            trendingShimmerState.postValue(ShimmerState.START)

            viewModelScope.launch {
                repository.fetchTrendingData(
                    trendingSeason,
                    TypeCategory.TV
                ) { result ->
                    trendingResultsItem.postValue(result)
                    trendingShimmerState.postValue(when {
                        result.isNullOrEmpty() -> ShimmerState.STOP_GONE
                        else -> ShimmerState.STOP_VISIBLE
                    })
                    isProcessGetTrendingData = false
                }
            }
        }
    }
}