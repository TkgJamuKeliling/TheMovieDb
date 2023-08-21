package com.zainal.moviedb.util

import android.content.Context
import com.google.gson.Gson
import com.zainal.moviedb.db.MovieDatabase
import com.zainal.moviedb.db.MovieEntity
import com.zainal.moviedb.model.CastItem
import com.zainal.moviedb.model.DetailResponse
import com.zainal.moviedb.model.GenresItem
import com.zainal.moviedb.model.ReviewItemResponse
import com.zainal.moviedb.model.ReviewResponse
import com.zainal.moviedb.model.ReviewResultsItem
import com.zainal.moviedb.model.TrendingResultsItem
import com.zainal.moviedb.model.VideoResultsItem
import com.zainal.moviedb.network.RestClientService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(
    var context: Context,
    private var restClientService: RestClientService
) {

    suspend fun fetchTrendingData(
        trendingSeason: TrendingSeason,
        typeCategory: TypeCategory,
        result: (List<TrendingResultsItem>?) -> Unit
    ) {
        coroutineScope {
            delay(1000L)

            val trendingResponseModel = async(Dispatchers.IO) {
                restClientService.getInstance().getTrendingData(
                    typeCategory.name.lowercase(),
                    trendingSeason.name.lowercase()
                )
            }.await()

            result(trendingResponseModel?.results?.filterNotNull())
        }
    }

    suspend fun fetchGenreData(
        typeCategory: TypeCategory,
        result: (List<GenresItem>?) -> Unit
    ) {
        coroutineScope {
            delay(1000L)
            val genreResponseModel = async(Dispatchers.IO) {
                restClientService.getInstance().getGenreData(typeCategory.name.lowercase())
            }.await()

            result(genreResponseModel?.genres?.apply {
                map { genresItem ->
                    genresItem?.icon = "ic_${genresItem?.name?.lowercase()?.replace(" ", "_")}_genre"
                }
            }?.filterNotNull())
        }
    }

    suspend fun fetchDetail(
        id: Int,
        page: Int,
        typeCategory: String,
        result: (
            DetailResponse?,
            List<VideoResultsItem>?,
            List<CastItem>?,
            ReviewResponse?,
            List<ReviewResultsItem>
        ) -> Unit
    ) {
        coroutineScope {
            delay(1000L)

            val detailResponse = async(Dispatchers.IO) {
                restClientService.getInstance().getDetailData(
                    typeCategory.lowercase(),
                    "$id"
                )
            }.await()

            val videoResponse = async(Dispatchers.IO) {
                restClientService.getInstance().getVideosData(
                    typeCategory.lowercase(),
                    "$id"
                )
            }.await()

            val castResponse = async(Dispatchers.IO) {
                restClientService.getInstance().getCastData(
                    typeCategory.lowercase(),
                    "$id"
                )
            }.await()

            val reviewResponse = async(Dispatchers.IO) {
                fetchReviews(
                    id,
                    page,
                    typeCategory
                )
            }.await()

            var listReviewResultsItem = emptyList<ReviewResultsItem>()
            reviewResponse?.results?.filterNotNull()?.let {
                listReviewResultsItem = it
            }

            result(
                detailResponse,
                videoResponse?.results?.filterNotNull(),
                castResponse?.cast?.filterNotNull(),
                reviewResponse,
                listReviewResultsItem
            )
        }
    }

    private suspend fun fetchReviews(
        id: Int,
        page: Int,
        typeCategory: String
    ) = restClientService.getInstance().getReviewsData(
        typeCategory.lowercase(),
        "$id",
        "$page"
    )

    suspend fun fetchMoreReviews(
        id: Int,
        page: Int,
        list: List<ReviewResultsItem>?,
        typeCategory: String,
        result: (ReviewResponse?, List<ReviewResultsItem>?) -> Unit
    ) {
        coroutineScope {
            delay(1000L)
            val reviewResponse = async(Dispatchers.IO) {
                fetchReviews(
                    id,
                    page,
                    typeCategory
                )
            }.await()

            val listReview = list?.toMutableList()
            reviewResponse?.results?.filterNotNull()?.let {
                listReview?.addAll(it)
            }

            result(reviewResponse, listReview)
        }
    }

    suspend fun fetchReviewDetail(
        reviewId: String,
        result: (ReviewItemResponse?) -> Unit
    ) {
        coroutineScope {
            delay(1000L)
            val reviewItemResponse = async(Dispatchers.IO) {
                restClientService.getInstance().getReviewDetail(reviewId)
            }.await()

            result(reviewItemResponse)
        }
    }

    suspend fun isFavExist(
        movieId: Int,
        result: (Boolean, MovieEntity?) -> Unit
    ) {
        coroutineScope {
            MovieDatabase.getInstance(context)?.let {
                val movieDao = it.movieDao()
                val movieEntity = async {
                    movieDao.isFavExist("$movieId")
                }.await()

                movieEntity?.let { entity ->
                    result(true, entity)
                    return@coroutineScope
                }
            }
            result(false, null)
        }
    }

    suspend fun updateDb(
        detailResponse: DetailResponse,
        result: (DbStateAction) -> Unit
    ) {
        coroutineScope {
            var flag = true
            MovieDatabase.getInstance(context)?.let {
                val movieDao = it.movieDao()

                isFavExist(detailResponse.id) { b, movieEntity ->
                    movieEntity?.let {
                        if (b) {
                            flag = false

                            //delete
                            launch {
                                withContext(Dispatchers.Default) {
                                    movieDao.deleteFav(movieEntity)
                                }
                                result(DbStateAction.SUCCESS_DELETE)
                            }
                        }
                    }

                    if (flag) {
                        //save
                        launch {
                            withContext(Dispatchers.Default) {
                                movieDao.insertFav(MovieEntity(
                                    movieId = detailResponse.id,
                                    movieDetail = Gson().toJson(detailResponse, DetailResponse::class.java)
                                ))
                            }
                            result(DbStateAction.SUCCESS_INSERT)
                        }
                    }
                }
                return@coroutineScope
            }
            if (flag) {
                result(DbStateAction.FAILED_ACTION)
            }
        }
    }
}