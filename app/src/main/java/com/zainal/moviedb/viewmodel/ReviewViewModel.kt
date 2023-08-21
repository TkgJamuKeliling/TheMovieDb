package com.zainal.moviedb.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.util.Repository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewViewModel(private var repository: Repository): BaseViewModel() {
    val title = MutableLiveData<String?>()
    fun vmTitle(): LiveData<String?> = title

    val subTitle = MutableLiveData<List<String>?>()
    fun vmSubTitle(): LiveData<List<String>?> = subTitle

    val content = MutableLiveData<String?>()
    fun vmContent(): LiveData<String?> = content

    fun getReviewDetail(
        reviewId: String,
        year: String?
    ) {
        viewModelScope.launch {
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
                }
            }
        }
    }
}