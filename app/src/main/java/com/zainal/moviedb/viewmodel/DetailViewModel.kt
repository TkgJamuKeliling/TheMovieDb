package com.zainal.moviedb.viewmodel

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.CastItem
import com.zainal.moviedb.model.DetailResponse
import com.zainal.moviedb.model.ReviewResponse
import com.zainal.moviedb.model.ReviewResultsItem
import com.zainal.moviedb.model.VideoResultsItem
import com.zainal.moviedb.util.DbStateAction
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.util.ShimmerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DetailViewModel(private var repository: Repository): BaseViewModel()
{
    private var isLoadingMoreReview = false

    val shimmerState = MutableLiveData<ShimmerState>()
    fun vmShimmerState(): LiveData<ShimmerState> = shimmerState

    val detailResponse = MutableLiveData<DetailResponse?>()

    val bgUrlPoster = MutableLiveData<String?>()
    fun vmBgUrlPoster(): LiveData<String?> = bgUrlPoster

    val urlPoster = MutableLiveData<String?>()
    fun vmUrlPoster(): LiveData<String?> = urlPoster

    val tag = MutableLiveData<String?>()
    fun vmTag(): LiveData<String?> = tag

    val title = MutableLiveData<List<String>>()
    fun vmTitle(): LiveData<List<String>> = title

    val genresItem = MutableLiveData<String?>()
    fun vmGenresItem(): LiveData<String?> = genresItem

    val voteAverage = MutableLiveData<Int?>()
    fun vmVoteAverage(): LiveData<Int?> = voteAverage

    val overview = MutableLiveData<String?>()
    fun vmOverview(): LiveData<String?> = overview

    val isFav = MutableLiveData<Boolean>()
    fun vmIsFav(): LiveData<Boolean> = isFav

    val videoResultItems = MutableLiveData<List<VideoResultsItem>?>()
    fun vmVideoResultItems(): LiveData<List<VideoResultsItem>?> = videoResultItems

    val castItems = MutableLiveData<List<CastItem>?>()
    fun vmCastItems(): LiveData<List<CastItem>?> = castItems

    val reviewResponse = MutableLiveData<ReviewResponse?>()

    val totalReview = MutableLiveData<Int>()
    fun vmTotalReview(): LiveData<Int> = totalReview

    val reviewItems = MutableLiveData<List<ReviewResultsItem>>()
    fun vmReviewItems(): LiveData<List<ReviewResultsItem>> = reviewItems

    val loadingView = MutableLiveData<Int>()
    fun vmLoadingView(): LiveData<Int> = loadingView

    val stuckView = MutableLiveData<Int>()
    fun vmStuckView(): LiveData<Int> = stuckView

    fun getDetailData(
        id: Int,
        page: Int,
        typeCategory: String,
    ) {
        shimmerState.postValue(ShimmerState.START)

        viewModelScope.launch {
            repository.fetchDetail(
                id,
                page + 1,
                typeCategory
            ) { mDetailResponse, mListVideo, mListCast, mReviewResponse, mListReviewItem ->
                detailResponse.postValue(mDetailResponse.also {
                    it?.let {
                        shimmerState.postValue(ShimmerState.STOP_GONE)

                        bgUrlPoster.postValue(it.backdropPath)

                        urlPoster.postValue(it.posterPath)

                        tag.postValue(it.tagline)

                        var flag = true
                        val mList = mutableListOf<String>()
                        it.originalTitle?.let { mTitle ->
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
                            isFav.postValue(isExist)
                        }

                        overview.postValue(it.overview)
                    }
                })

                videoResultItems.postValue(mListVideo)

                castItems.postValue(mListCast)

                reviewResponse.postValue(mReviewResponse)
                totalReview.postValue(mReviewResponse?.totalResults ?:0)

                reviewItems.postValue(mListReviewItem)
            }
        }
    }

    fun getMoreReviewsData(typeCategory: String) {
        viewModelScope.launch {
            reviewResponse.value?.let {
                if (!isLoadingMoreReview) {
                    isLoadingMoreReview = true

                    loadingView.postValue(VISIBLE)

                    if (it.totalPages > it.page) {
                        repository.fetchMoreReviews(
                            it.id,
                            it.page,
                            reviewItems.value,
                            typeCategory
                        ) { mReviewResponse, mListReviewItem ->
                            reviewResponse.postValue(mReviewResponse)
                            totalReview.postValue(mReviewResponse?.totalResults ?:0)

                            loadingView.postValue(INVISIBLE)
                            reviewItems.postValue(mListReviewItem)

                            isLoadingMoreReview = false
                        }
                        return@launch
                    }

                    delay(1000L)
                    loadingView.postValue(INVISIBLE)
                    stuckView.postValue(VISIBLE)

                    delay(1000L)
                    stuckView.postValue(INVISIBLE)
                    isLoadingMoreReview = false
                }
            }
        }
    }

    fun updateFav() {
        detailResponse.value?.let {
            viewModelScope.launch {
                repository.updateDb(it) { dbStateAction ->
                    isFav.postValue(dbStateAction == DbStateAction.SUCCESS_INSERT)
                }
            }
        }
    }
}