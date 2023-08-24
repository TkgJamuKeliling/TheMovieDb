package com.zainal.moviedb.viewmodel

import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.R
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TrendingSeason
import com.zainal.moviedb.util.TypeCategory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class TvShowViewModel(repository: Repository) : MainFragmentViewModel(repository) {
    private var isProcessGetAllData = false
    private var isProcessGetTrendingData = false

    private fun exceptionHandler(state: (ShimmerState, String?) -> Unit): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            var msg = repository.context.getString(R.string.default_error_msg)
            throwable.message?.let {
                msg = it
            }
            state(
                ShimmerState.STOP_VISIBLE,
                msg
            )
            isProcessGetAllData = false
            isProcessGetTrendingData = false
        }
    }

    fun fetchAllData(
        trendingSeason: TrendingSeason,
        state: (ShimmerState, String?) -> Unit
    ) {
        if (!isProcessGetAllData && !isProcessGetTrendingData) {
            isProcessGetAllData = true

            genreResponse.postValue(null)
            trendingResultsItem.postValue(null)
            genresItem.postValue(null)

            state(
                ShimmerState.START,
                null
            )

            viewModelScope.launch(exceptionHandler { shimmerState, s ->
                state(
                    shimmerState,
                    s
                )
            }) {
                repository.fetchMainFragmentData(
                    trendingSeason,
                    TypeCategory.TV
                ) { mTrendingResultsItem, mGenreResponse, mGenresItem ->
                    genreResponse.postValue(mGenreResponse)
                    trendingResultsItem.postValue(mTrendingResultsItem)
                    genresItem.postValue(mGenresItem)

                    state(
                        ShimmerState.STOP_GONE,
                        null
                    )

                    isProcessGetAllData = false
                }
            }
        }
    }

    fun getTvTrendingData(
        trendingSeason: TrendingSeason,
        state: (ShimmerState, String?) -> Unit
    ) {
        if (!isProcessGetTrendingData) {
            isProcessGetTrendingData = true

            trendingResultsItem.postValue(null)

            state(
                ShimmerState.START,
                null
            )

            viewModelScope.launch(exceptionHandler { shimmerState, s ->
                state(
                    shimmerState,
                    s
                )
            }) {
                repository.fetchTrendingData(
                    trendingSeason,
                    TypeCategory.TV
                ) { result ->
                    trendingResultsItem.postValue(result)

                    state(
                        when {
                            result.isNullOrEmpty() -> ShimmerState.STOP_VISIBLE
                            else -> ShimmerState.STOP_GONE
                        },
                        null
                    )

                    isProcessGetTrendingData = false
                }
            }
        }
    }
}