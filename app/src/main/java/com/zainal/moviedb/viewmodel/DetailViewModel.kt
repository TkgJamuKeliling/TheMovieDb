package com.zainal.moviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.response.CastItem
import com.zainal.moviedb.model.response.DetailResponse
import com.zainal.moviedb.model.response.ReviewResponse
import com.zainal.moviedb.model.response.ReviewResultsItem
import com.zainal.moviedb.model.response.VideoResultsItem
import com.zainal.moviedb.util.BottomViewState
import com.zainal.moviedb.util.DbStateAction
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TypeCategory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DetailViewModel(private var repository: Repository): BaseViewModel()
{
    private var isLoadingMoreReview = false
    private var isProcessGetDetail = false

    private val detailResponse = MutableLiveData<DetailResponse?>()

    private val bgUrlPoster = MutableLiveData<String?>()
    fun vmBgUrlPoster(): LiveData<String?> = bgUrlPoster

    val urlPoster = MutableLiveData<String?>()
    fun vmUrlPoster(): LiveData<String?> = urlPoster

    private val tag = MutableLiveData<String?>()
    fun vmTag(): LiveData<String?> = tag

    val title = MutableLiveData<List<String>>()
    fun vmTitle(): LiveData<List<String>> = title

    private val genresItem = MutableLiveData<String?>()
    fun vmGenresItem(): LiveData<String?> = genresItem

    private val voteAverage = MutableLiveData<Int?>()
    fun vmVoteAverage(): LiveData<Int?> = voteAverage

    private val overview = MutableLiveData<String?>()
    fun vmOverview(): LiveData<String?> = overview

    private val isFav = MutableLiveData<Int>()
    fun vmIsFav(): LiveData<Int> = isFav

    private val videoResultItems = MutableLiveData<List<VideoResultsItem>?>()
    fun vmVideoResultItems(): LiveData<List<VideoResultsItem>?> = videoResultItems

    private val castItems = MutableLiveData<List<CastItem>?>()
    fun vmCastItems(): LiveData<List<CastItem>?> = castItems

    private val reviewResponse = MutableLiveData<ReviewResponse?>()

    private val totalReview = MutableLiveData<Int>()
    fun vmTotalReview(): LiveData<Int> = totalReview

    private val reviewItems = MutableLiveData<List<ReviewResultsItem>>()
    fun vmReviewItems(): LiveData<List<ReviewResultsItem>> = reviewItems

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
            isLoadingMoreReview = false
        }
    }

    fun getDetailData(
        id: Int,
        page: Int,
        typeCategory: String,
        state: (ShimmerState, String?) -> Unit
    ) {
        if (!isProcessGetDetail) {
            isProcessGetDetail = true

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
                voteAverage.postValue(0)

                repository.fetchDetail(
                    id,
                    page + 1,
                    typeCategory.lowercase()
                ) { mDetailResponse, mListVideo, mListCast, mReviewResponse, mListReviewItem ->
                    detailResponse.postValue(mDetailResponse.also {
                        it?.let {
                            bgUrlPoster.postValue(it.backdropPath)

                            urlPoster.postValue(it.posterPath)

                            tag.postValue(it.tagline)

                            var flag = true
                            val mList = mutableListOf<String>()
                            when (typeCategory.lowercase()) {
                                TypeCategory.MOVIE.name.lowercase() -> it.title?.let { mTitle ->
                                    it.releaseDate?.let { mDate ->
                                        flag = false
                                        mList.apply {
                                            add(mTitle)
                                            add(mDate.substringBefore("-"))
                                        }
                                    }
                                    if (flag) {
                                        flag = false
                                        mList.add(mTitle)
                                    }
                                }

                                else -> it.name?.let { mTitle ->
                                    it.firstAirDate?.let { mDate ->
                                        flag = false
                                        mList.apply {
                                            add(mTitle)
                                            add(mDate.substringBefore("-"))
                                        }
                                    }
                                    if (flag) {
                                        flag = false
                                        mList.add(mTitle)
                                    }
                                }
                            }
                            title.postValue(mList)

                            it.genres?.filterNotNull()?.joinToString(", ") { item ->
                                item.name!!
                            }.also { s ->
                                genresItem.postValue(s)
                            }

                            it.voteAverage?.let { i ->
                                voteAverage.postValue((i * 10).roundToInt())
                            }

                            viewModelScope.launch {
                                var isExist = false
                                repository.isFavExist(it.id) { b, movieEntity ->
                                    movieEntity?.let {
                                        isExist = b
                                    }
                                }
                                isFav.postValue(when {
                                    isExist -> R.color.yellow
                                    else -> R.color.white
                                })
                            }

                            overview.postValue(it.overview)
                        }
                    })

                    videoResultItems.postValue(mListVideo)

                    castItems.postValue(mListCast)

                    reviewResponse.postValue(mReviewResponse)
                    totalReview.postValue(mReviewResponse?.totalResults ?:0)

                    reviewItems.postValue(mListReviewItem)

                    state(
                        ShimmerState.STOP_GONE,
                        null
                    )

                    isProcessGetDetail = false
                }
            }
        }
    }

    fun getMoreReviewsData(
        typeCategory: String,
        state: (BottomViewState, String?) -> Unit
    ) {
        viewModelScope.launch(exceptionHandler { _, s ->
            state(
                BottomViewState.NORMAL,
                s
            )
        }) {
            reviewResponse.value?.let {
                reviewItems.value?.let { resultsItems ->
                    if (resultsItems.isNotEmpty()) {
                        if (!isLoadingMoreReview) {
                            isLoadingMoreReview = true

                            state(
                                BottomViewState.LOADING,
                                null
                            )

                            if (it.totalPages > it.page) {
                                repository.fetchMoreReviews(
                                    it.id,
                                    it.page + 1,
                                    resultsItems,
                                    typeCategory
                                ) { mReviewResponse, mListReviewItem ->
                                    reviewResponse.postValue(mReviewResponse)
                                    totalReview.postValue(mReviewResponse?.totalResults ?:0)

                                    state(
                                        BottomViewState.NORMAL,
                                        null
                                    )

                                    reviewItems.postValue(mListReviewItem)
                                    isLoadingMoreReview = false
                                }
                                return@launch
                            }

                            delay(1000L)
                            state(
                                BottomViewState.STUCK,
                                null
                            )

                            delay(1000L)
                            state(
                                BottomViewState.NORMAL,
                                null
                            )

                            isLoadingMoreReview = false
                        }
                    }
                }
            }
        }
    }

    fun updateFav(typeCategory: String) {
        detailResponse.value?.let {
            viewModelScope.launch {
                repository.updateDb(
                    it,
                    typeCategory
                ) { dbStateAction ->
                    isFav.postValue(when (dbStateAction) {
                        DbStateAction.SUCCESS_INSERT -> R.color.yellow
                        else -> R.color.white
                    })
                }
            }
        }
    }
}