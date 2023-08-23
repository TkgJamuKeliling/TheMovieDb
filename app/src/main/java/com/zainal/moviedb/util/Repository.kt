package com.zainal.moviedb.util

import android.content.Context
import com.google.gson.Gson
import com.zainal.moviedb.db.MovieDatabase
import com.zainal.moviedb.db.MovieEntity
import com.zainal.moviedb.model.response.CastItem
import com.zainal.moviedb.model.response.DetailResponse
import com.zainal.moviedb.model.response.DiscoverResponse
import com.zainal.moviedb.model.response.DiscoverResultsItem
import com.zainal.moviedb.model.response.GenreResponse
import com.zainal.moviedb.model.response.GenresItem
import com.zainal.moviedb.model.response.ReviewItemResponse
import com.zainal.moviedb.model.response.ReviewResponse
import com.zainal.moviedb.model.response.ReviewResultsItem
import com.zainal.moviedb.model.response.TrendingResultsItem
import com.zainal.moviedb.model.response.VideoResultsItem
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

    suspend fun fetchMainFragmentData(
        trendingSeason: TrendingSeason,
        typeCategory: TypeCategory,
        result: (List<TrendingResultsItem>?, GenreResponse?, List<GenresItem>?) -> Unit
    ) {
        coroutineScope {
            delay(1000L)

            val trendingResponse = async(Dispatchers.IO) {
                fetchTrendingResponse(
                    typeCategory,
                    trendingSeason
                )
            }.await()

            val genreResponse = async(Dispatchers.IO) {
                fetchGenreResponse(typeCategory)
            }.await()

            result(
                trendingResponse?.results?.filterNotNull(),
                genreResponse,
                genreResponse?.genres?.filterNotNull()?.apply {
                    map {
                        it.icon = iconName(it.name)
                    }
                }
            )
        }
    }

    private fun iconName(s: String?): String {
        s?.let {
            return buildString {
                append("ic_")
                append(
                    it.lowercase()
                        .replace(" ", "_")
                        .replace("-", "_")
                        .replace("&", "")
                        .replace("__", "_")
                )
                append("_genre")
            }
        }
        return ""
    }

    private suspend fun fetchTrendingResponse(
        typeCategory: TypeCategory,
        trendingSeason: TrendingSeason
    ) = restClientService.getInstance().getTrendingData(
        typeCategory.name.lowercase(),
        trendingSeason.name.lowercase()
    )

    private suspend fun fetchGenreResponse(
        typeCategory: TypeCategory
    ) = restClientService.getInstance().getGenreData(typeCategory.name.lowercase())

    suspend fun fetchTrendingData(
        trendingSeason: TrendingSeason,
        typeCategory: TypeCategory,
        result: (List<TrendingResultsItem>?) -> Unit
    ) {
        coroutineScope {
            delay(1000L)

            val trendingResponse = async(Dispatchers.IO) {
                fetchTrendingResponse(
                    typeCategory,
                    trendingSeason,
                )
            }.await()

            result(trendingResponse?.results?.filterNotNull())
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
            getMovieDao()?.let {
                val movieEntity = async {
                    it.isFavExist("$movieId")
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
        typeCategory: String,
        result: (DbStateAction) -> Unit
    ) {
        coroutineScope {
            var flag = true
            getMovieDao()?.let {
                isFavExist(detailResponse.id) { b, movieEntity ->
                    movieEntity?.let { _ ->
                        if (b) {
                            flag = false

                            //delete
                            launch {
                                withContext(Dispatchers.Default) {
                                    it.deleteFav(movieEntity)
                                }
                                result(DbStateAction.SUCCESS_DELETE)
                            }
                        }
                    }

                    if (flag) {
                        flag = false

                        //save
                        launch {
                            withContext(Dispatchers.Default) {
                                it.insertFav(MovieEntity(
                                    movieId = detailResponse.id,
                                    movieDetail = Gson().toJson(detailResponse, DetailResponse::class.java),
                                    typeCategory = typeCategory.uppercase()
                                ))
                            }
                            result(DbStateAction.SUCCESS_INSERT)
                        }
                    }
                }
            }

            if (flag) {
                result(DbStateAction.FAILED_ACTION)
            }
        }
    }

    suspend fun fetchDiscover(
        genreId: Int,
        page: Int,
        typeCategory: String,
        result: (DiscoverResponse?, List<DiscoverResultsItem>?) -> Unit
    ) {
        coroutineScope {
            val discoverResponse = async(Dispatchers.IO) {
                when (typeCategory) {
                    TypeCategory.MOVIE.name -> restClientService.getInstance().getMovieDiscoverData(
                        "$page",
                        "$genreId"
                    )

                    else -> restClientService.getInstance().getTvDiscoverData(
                        "$page",
                        "$genreId"
                    )
                }
            }.await()

            result(
                discoverResponse,
                discoverResponse?.results?.filterNotNull()
            )
        }
    }

    fun getMovieDao() = MovieDatabase.getInstance(context)?.movieDao()

    suspend fun deleteFavItem(movieEntity: MovieEntity) = coroutineScope {
        getMovieDao()?.deleteFav(movieEntity)
    }

    suspend fun restoreFavItem(movieEntity: MovieEntity) = coroutineScope {
        getMovieDao()?.insertFav(movieEntity)
    }
}