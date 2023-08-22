package com.zainal.moviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.DiscoverResponse
import com.zainal.moviedb.model.DiscoverResultsItem
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverViewModel(private var repository: Repository): BaseViewModel() {
    var isFirstRequestProcess = false

    val shimmerState = MutableLiveData<ShimmerState>()
    fun vmShimmerState(): LiveData<ShimmerState> = shimmerState

    val discoverResponse = MutableLiveData<DiscoverResponse?>()

    val discoverResultsItem = MutableLiveData<List<DiscoverResultsItem>?>()
    fun vmDiscoverResultsItem(): LiveData<List<DiscoverResultsItem>?> = discoverResultsItem

    fun getDiscoverData(
        genreId: Int,
        typeCategory: String,
        firstRequest: Boolean = true
    ) {
        viewModelScope.launch {
            if (firstRequest && isFirstRequestProcess) {
                return@launch
            }

            if (firstRequest) {
                shimmerState.postValue(ShimmerState.START)
                discoverResultsItem.postValue(null)
            }

            var page = 1
            if (!firstRequest) {
                discoverResponse.value?.page?.let {
                    page = it + 1
                }
            }

            val currentListData = discoverResultsItem.value?.toMutableList()

            if (firstRequest) {
                isFirstRequestProcess = true
            }
            repository.fetchDiscover(
                genreId,
                page,
                typeCategory,
            ) { mDiscoverResponse, mDiscoverResultsItem ->
                discoverResponse.postValue(mDiscoverResponse)
                discoverResultsItem.postValue(when {
                    firstRequest -> mDiscoverResultsItem
                    else -> currentListData?.apply {
                        mDiscoverResultsItem?.let {
                            addAll(it)
                        }
                        toList()
                    }
                })

                if (firstRequest) {
                    isFirstRequestProcess = false
                }
                shimmerState.postValue(ShimmerState.STOP_GONE)
            }
        }
    }
}