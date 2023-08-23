package com.zainal.moviedb.network

import com.zainal.moviedb.model.response.CastResponse
import com.zainal.moviedb.model.response.DetailResponse
import com.zainal.moviedb.model.response.DiscoverResponse
import com.zainal.moviedb.model.response.GenreResponse
import com.zainal.moviedb.model.response.ReviewItemResponse
import com.zainal.moviedb.model.response.ReviewResponse
import com.zainal.moviedb.model.response.TrendingResponse
import com.zainal.moviedb.model.response.VideoResponse
import com.zainal.moviedb.util.Constant.CAST_ENDPOINT
import com.zainal.moviedb.util.Constant.DETAIL_ENDPOINT
import com.zainal.moviedb.util.Constant.DISCOVER_MOVIE_ENDPOINT
import com.zainal.moviedb.util.Constant.DISCOVER_TV_ENDPOINT
import com.zainal.moviedb.util.Constant.GENRES_ENDPOINT
import com.zainal.moviedb.util.Constant.REVIEWS_ENDPOINT
import com.zainal.moviedb.util.Constant.REVIEW_DETAIL_ENDPOINT
import com.zainal.moviedb.util.Constant.TRENDINGS_ENDPOINT
import com.zainal.moviedb.util.Constant.VIDEOS_ENDPOINT
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET(GENRES_ENDPOINT)
    suspend fun getGenreData(
        @Path(value = "xCategory") xCategory: String
    ): GenreResponse?

    @GET(TRENDINGS_ENDPOINT)
    suspend fun getTrendingData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xSession") xSession: String
    ): TrendingResponse?

    @GET(DETAIL_ENDPOINT)
    suspend fun getDetailData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String
    ): DetailResponse?

    @GET(REVIEWS_ENDPOINT)
    suspend fun getReviewsData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String,
        @Query("page") xPage: String
    ): ReviewResponse?

    @GET(REVIEW_DETAIL_ENDPOINT)
    suspend fun getReviewDetail(
        @Path(value = "xId") xId: String
    ): ReviewItemResponse?

    @GET(VIDEOS_ENDPOINT)
    suspend fun getVideosData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String
    ): VideoResponse?

    @GET(CAST_ENDPOINT)
    suspend fun getCastData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String
    ): CastResponse?

    @GET(DISCOVER_MOVIE_ENDPOINT)
    suspend fun getMovieDiscoverData(
        @Query(value = "page") page: String,
        @Query(value = "with_genres", encoded = true) genreId: String
    ): DiscoverResponse?

    @GET(DISCOVER_TV_ENDPOINT)
    suspend fun getTvDiscoverData(
        @Query(value = "page") page: String,
        @Query(value = "with_genres", encoded = true) genreId: String
    ): DiscoverResponse?
}