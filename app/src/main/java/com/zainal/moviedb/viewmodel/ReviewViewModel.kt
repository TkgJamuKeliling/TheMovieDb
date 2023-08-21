package com.zainal.moviedb.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewViewModel(private var repository: Repository): BaseViewModel() {
    var isProcessGetDetail = false

    val title = MutableLiveData<String?>()
    fun vmTitle(): LiveData<String?> = title

    val subTitle = MutableLiveData<List<String>?>()
    fun vmSubTitle(): LiveData<List<String>?> = subTitle

    val content = MutableLiveData<String?>()
    fun vmContent(): LiveData<String?> = content

    val shimmerState = MutableLiveData<ShimmerState>()
    fun vmShimmerState(): LiveData<ShimmerState> = shimmerState

    fun getReviewDetail(
        reviewId: String,
        year: String?,
        enable: (Boolean) -> Unit
    ) {
        if (!isProcessGetDetail) {
            isProcessGetDetail = true
            shimmerState.postValue(ShimmerState.START)

            viewModelScope.launch {
                enable(false)

                repository.fetchReviewDetail(
                    reviewId
                ) { reviewItemResponse ->

                    shimmerState.postValue(ShimmerState.STOP_GONE)

                    reviewItemResponse?.let {
                        title.postValue(buildString {
                            append(it.mediaTitle)
                            year?.let { s ->
                                append("($s)")
                            }
                        })

                        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        subTitle.postValue(listOfNotNull(
                            it.author,
                            it.updatedAt?.substringBefore("T")?.let { s ->
                                parser.parse(s)?.let { date ->
                                    formatter.format(date)
                                }
                            }
                        ))
                        content.postValue(it.content)
                        enable(true)
                        isProcessGetDetail = false
                        return@fetchReviewDetail
                    }
                    shimmerState.postValue(ShimmerState.STOP_VISIBLE)
                    enable(true)
                    isProcessGetDetail = false
                }
            }
        }
    }
}