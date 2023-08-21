package com.zainal.moviedb.viewmodel

import android.content.Context
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.GenresItem
import com.zainal.moviedb.model.TrendingResultsItem
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.util.TrendingSeason
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: Repository) : BaseViewModel()
{

    private val listTrendingTrendingResultsItem = MutableLiveData<List<TrendingResultsItem>?>()
    fun vmListTrendingResultsItem(): LiveData<List<TrendingResultsItem>?> = listTrendingTrendingResultsItem

    private val listGenresItem = MutableLiveData<List<GenresItem>?>()
    fun vmListGenreItem(): LiveData<List<GenresItem>?> = listGenresItem

    private val rcvTrendingState = MutableLiveData<Int>()
    fun vmRcvTrendingState(): LiveData<Int> = rcvTrendingState

    private val rcvGenreState = MutableLiveData<Int>()
    fun vmRcvGenreState(): LiveData<Int> = rcvGenreState

    private val trendingShimmerState = MutableLiveData<ShimmerState>()
    fun vmTrendingShimmerState(): LiveData<ShimmerState> = trendingShimmerState

    private val genreShimmerState = MutableLiveData<ShimmerState>()
    fun vmGenreShimmerState(): LiveData<ShimmerState> = genreShimmerState

    private val seasonBtnState = MutableLiveData<Boolean>()
    fun vmSeasonBtnState(): LiveData<Boolean> = seasonBtnState

    override fun fetchAllData(trendingSeason: TrendingSeason) {
        super.fetchAllData(trendingSeason)

        getMovieTrendingData(trendingSeason)
        getGenreData()
    }

    private fun getGenreData() {
        if (!isGenreRequestProcess) {
            isGenreRequestProcess = true

            rcvGenreState.postValue(INVISIBLE)
            genreShimmerState.postValue(ShimmerState.START)
            listGenresItem.postValue(null)

            viewModelScope.launch {
                repository.fetchGenreData(TypeCategory.MOVIE) { result ->
                    isGenreRequestProcess = false
                    when {
                        result.isNullOrEmpty() -> genreShimmerState.postValue(ShimmerState.STOP_VISIBLE)
                        else -> {
                            genreShimmerState.postValue(ShimmerState.STOP_GONE)
                            rcvGenreState.postValue(VISIBLE)
                            listGenresItem.postValue(result)
                        }
                    }
                }
            }
        }
    }

    fun getMovieTrendingData(trendingSeason: TrendingSeason) {
        if (!isTrendingRequestProcess) {
            isTrendingRequestProcess = true

            seasonBtnState.postValue(false)
            rcvTrendingState.postValue(INVISIBLE)
            listTrendingTrendingResultsItem.postValue(null)
            trendingShimmerState.postValue(ShimmerState.START)

            viewModelScope.launch {
                repository.fetchTrendingData(
                    trendingSeason,
                    TypeCategory.MOVIE
                ) { result ->
                    isTrendingRequestProcess = false
                    seasonBtnState.postValue(true)
                    when {
                        result.isNullOrEmpty() -> trendingShimmerState.postValue(ShimmerState.STOP_VISIBLE)
                        else -> {
                            trendingShimmerState.postValue(ShimmerState.STOP_GONE)
                            rcvTrendingState.postValue(VISIBLE)
                            listTrendingTrendingResultsItem.postValue(result)
                        }
                    }
                }
            }
        }
    }
}