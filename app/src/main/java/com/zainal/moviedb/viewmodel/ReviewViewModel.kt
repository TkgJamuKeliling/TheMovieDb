package com.zainal.moviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewViewModel(private var repository: Repository): BaseViewModel() {
    private var isProcessGetDetail = false

    val title = MutableLiveData<String?>()
    fun vmTitle(): LiveData<String?> = title

    private val subTitle = MutableLiveData<List<String>?>()
    fun vmSubTitle(): LiveData<List<String>?> = subTitle

    val content = MutableLiveData<String?>()
    fun vmContent(): LiveData<String?> = content

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
            isProcessGetDetail = false
        }
    }

    fun getReviewDetail(
        reviewId: String,
        year: String?,
        state: (Boolean, ShimmerState, String?) -> Unit
    ) {
        if (!isProcessGetDetail) {
            isProcessGetDetail = true

            state(
                false,
                ShimmerState.START,
                null
            )

            viewModelScope.launch(exceptionHandler { shimmerState, s ->
                state(
                    true,
                    shimmerState,
                    s
                )
            }) {
                repository.fetchReviewDetail(
                    reviewId
                ) { reviewItemResponse ->
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

                        state(
                            true,
                            ShimmerState.STOP_GONE,
                            null
                        )

                        isProcessGetDetail = false
                        return@fetchReviewDetail
                    }
                    state(
                        true,
                        ShimmerState.STOP_VISIBLE,
                        null
                    )
                    isProcessGetDetail = false
                }
            }
        }
    }
}