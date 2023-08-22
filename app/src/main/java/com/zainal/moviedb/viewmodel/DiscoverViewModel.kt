package com.zainal.moviedb.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.DiscoverResponse
import com.zainal.moviedb.model.DiscoverResultsItem
import com.zainal.moviedb.util.BottomViewState
import com.zainal.moviedb.util.MenuState
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ScrollState
import com.zainal.moviedb.util.ShimmerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverViewModel(private var repository: Repository): BaseViewModel() {
    var isFirstRequestProcess = false
    var isMoreRequestProcess = false

    val discoverResponse = MutableLiveData<DiscoverResponse?>()

    val discoverResultsItem = MutableLiveData<List<DiscoverResultsItem>?>()
    fun vmDiscoverResultsItem(): LiveData<List<DiscoverResultsItem>?> = discoverResultsItem

    fun getDiscoverData(
        genreId: Int,
        typeCategory: String,
        isPageOne: Boolean,
        stateView: (
            ShimmerState,
            ScrollState,
            BottomViewState,
            MenuState
        ) -> Unit
    ) {
        viewModelScope.launch {
            if (isPageOne) {
                if (isFirstRequestProcess) {
                    return@launch
                }
            } else if (isMoreRequestProcess) {
                return@launch
            }

            stateView(
                when {
                    isPageOne -> ShimmerState.START
                    else -> ShimmerState.STOP_GONE
                },
                when {
                    isPageOne -> ScrollState.DISABLE
                    else -> ScrollState.DISABLE
                },
                when {
                    isPageOne -> BottomViewState.NORMAL
                    else -> BottomViewState.LOADING.also {
                        Log.w("Zen", "loading")
                    }
                },
                when {
                    isPageOne -> MenuState.DISABLE
                    else -> MenuState.ENABLE
                }
            )

            if (isPageOne) {
                discoverResponse.postValue(null)
                discoverResultsItem.postValue(null)
            }

            delay(1000L)

            if (!isPageOne) {
                var flag = false
                discoverResponse.value?.let {
                    if (it.page >= it.totalPages) {
                        stateView(
                            ShimmerState.STOP_GONE,
                            ScrollState.ENABLE,
                            BottomViewState.STUCK,
                            MenuState.ENABLE
                        )

                        delay(1000L)

                        stateView(
                            ShimmerState.STOP_GONE,
                            ScrollState.ENABLE,
                            BottomViewState.NORMAL,
                            MenuState.ENABLE
                        )
                        return@launch
                    } else {
                        flag = true
                    }
                }
                if (!flag) return@launch
            }

            var page = 1
            if (!isPageOne) {
                discoverResponse.value?.page?.let {
                    page = it + 1
                }
            }

            val currentListData = discoverResultsItem.value?.toMutableList()

            when {
                isPageOne -> isFirstRequestProcess = true
                else -> isMoreRequestProcess = true
            }

            repository.fetchDiscover(
                genreId,
                page,
                typeCategory,
            ) { mDiscoverResponse, mDiscoverResultsItem ->
                discoverResponse.postValue(mDiscoverResponse)

                stateView(
                    ShimmerState.STOP_GONE,
                    ScrollState.ENABLE,
                    BottomViewState.NORMAL,
                    MenuState.ENABLE
                )

                discoverResultsItem.postValue(when {
                    isPageOne -> mDiscoverResultsItem
                    else -> currentListData?.apply {
                        if (!isFirstRequestProcess) {
                            mDiscoverResultsItem?.let {
                                addAll(it)
                            }
                            isMoreRequestProcess = false
                        }
                        toList()
                    }
                })

                when {
                    isPageOne -> isFirstRequestProcess = false
                    else -> isMoreRequestProcess = false
                }
            }
        }
    }
}