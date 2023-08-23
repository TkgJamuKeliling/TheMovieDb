package com.zainal.moviedb.network

import com.zainal.moviedb.model.response.CastResponse
import com.zainal.moviedb.model.response.DetailResponse
import com.zainal.moviedb.model.response.DiscoverResponse
import com.zainal.moviedb.model.response.GenreResponse
import com.zainal.moviedb.model.response.ReviewItemResponse
import com.zainal.moviedb.model.response.ReviewResponse
import com.zainal.moviedb.model.response.TrendingResponse
import com.zainal.moviedb.model.response.VideoResponse
import com.zainal.moviedb.util.Constant
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET(Constant.GENRES_ENDPOINT)
    suspend fun getGenreData(
        @Path(value = "xCategory") xCategory: String
    ): GenreResponse?

    @GET(Constant.TRENDINGS_ENDPOINT)
    suspend fun getTrendingData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xSession") xSession: String
    ): TrendingResponse?

    @GET(Constant.DETAIL_ENDPOINT)
    suspend fun getDetailData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String
    ): DetailResponse?

    @GET(Constant.REVIEWS_ENDPOINT)
    suspend fun getReviewsData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String,
        @Query("page") xPage: String
    ): ReviewResponse?

    @GET(Constant.REVIEW_DETAIL_ENDPOINT)
    suspend fun getReviewDetail(
        @Path(value = "xId") xId: String
    ): ReviewItemResponse?

    @GET(Constant.VIDEOS_ENDPOINT)
    suspend fun getVideosData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String
    ): VideoResponse?

    @GET(Constant.CAST_ENDPOINT)
    suspend fun getCastData(
        @Path(value = "xCategory") xCategory: String,
        @Path(value = "xId") xId: String
    ): CastResponse?

    @GET(Constant.DISCOVER_MOVIE_ENDPOINT)
    suspend fun getMovieDiscoverData(
        @Query(value = "page") page: String,
        @Query(value = "with_genres", encoded = true) genreId: String
    ): DiscoverResponse?

    @GET(Constant.DISCOVER_TV_ENDPOINT)
    suspend fun getTvDiscoverData(
        @Query(value = "page") page: String,
        @Query(value = "with_genres", encoded = true) genreId: String
    ): DiscoverResponse?
}