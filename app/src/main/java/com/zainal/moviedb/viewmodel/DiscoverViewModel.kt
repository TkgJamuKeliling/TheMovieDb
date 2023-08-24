package com.zainal.moviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.response.DiscoverResponse
import com.zainal.moviedb.model.response.DiscoverResultsItem
import com.zainal.moviedb.util.BottomViewState
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ScrollState
import com.zainal.moviedb.util.ShimmerState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverViewModel(private var repository: Repository): BaseViewModel() {
    private var isFirstRequestProcess = false
    private var isMoreRequestProcess = false

    private val discoverResponse = MutableLiveData<DiscoverResponse?>()

    private val discoverResultsItem = MutableLiveData<List<DiscoverResultsItem>?>()
    fun vmDiscoverResultsItem(): LiveData<List<DiscoverResultsItem>?> = discoverResultsItem

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
            isFirstRequestProcess = false
            isMoreRequestProcess = false
        }
    }

    fun getDiscoverData(
        genreId: Int,
        typeCategory: String,
        isPageOne: Boolean,
        stateView: (
            ShimmerState,
            ScrollState,
            BottomViewState,
            String?
        ) -> Unit
    ) {
        viewModelScope.launch(exceptionHandler { shimmerState, s ->
            stateView(
                when {
                    isPageOne -> shimmerState
                    else -> ShimmerState.STOP_GONE
                },
                ScrollState.DISABLE,
                when {
                    isPageOne -> BottomViewState.NORMAL
                    else -> BottomViewState.LOADING
                },
                s
            )
        }) {
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
                ScrollState.DISABLE,
                when {
                    isPageOne -> BottomViewState.NORMAL
                    else -> BottomViewState.LOADING
                },
                null
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
                            null
                        )

                        delay(1000L)

                        stateView(
                            ShimmerState.STOP_GONE,
                            ScrollState.ENABLE,
                            BottomViewState.NORMAL,
                            null
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
                    null
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