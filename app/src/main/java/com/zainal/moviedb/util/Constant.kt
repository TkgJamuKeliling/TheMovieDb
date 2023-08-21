package com.zainal.moviedb.util

object Constant {
    const val KEY_PREFS = "PREFS"
    const val PREFS_FILE_NAME = "moviePrefs"
    const val PREFS_API_KEY = "apiKey"
    const val PREFS_ACCESS_TOKEN = "accessToken"

    const val EXTRA_ID = "extraId"
    const val EXTRA_CATEGORY = "extraCategory"
    const val EXTRA_REVIEW_ID = "extraReviewId"
    const val EXTRA_YEAR = "extraYear"
    const val EXTRA_POSTER_PATH = "extraPosterPath"

    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val BASE_URL_POSTER = "https://www.themoviedb.org/t/p/w1280"
    const val BASE_URL_AVATAR = "https://www.themoviedb.org/t/p/w90_and_h90_face"
    const val TRENDINGS_ENDPOINT = "trending/{xCategory}/{xSession}?language=en-US"
    const val GENRES_ENDPOINT = "genre/{xCategory}/list?language=en"
    const val DETAIL_ENDPOINT = "{xCategory}/{xId}?language=en-US"
    const val REVIEWS_ENDPOINT = "{xCategory}/{xId}/reviews?language=en-US"
    const val VIDEOS_ENDPOINT = "{xCategory}/{xId}/videos?language=en-US"
    const val CAST_ENDPOINT = "{xCategory}/{xId}/credits?language=en-US"
    const val REVIEW_DETAIL_ENDPOINT = "review/{xId}"



    const val DISCOVER_MOVIE_ENDPOINT = "discover/movie?include_adult=false&include_video=true&language=en-US&page={xPage}&sort_by=popularity.desc&with_genres={xGenre}"
    const val DISCOVER_TV_ENDPOINT = "discover/tv?include_adult=false&include_null_first_air_dates=false&language=en-US&page={xPage}&sort_by=popularity.desc&with_genres={xGenre}"

}

enum class TrendingSeason {
    DAY,
    WEEK
}

enum class TypeCategory {
    MOVIE,
    TV
}

enum class ShimmerState {
    START,
    STOP_VISIBLE,
    STOP_GONE
}

enum class ReviewSection(var type: Int) {
    DATA(0),
    LOADING(1)
}

enum class DbStateAction {
    SUCCESS_INSERT,
    SUCCESS_DELETE,
    FAILED_ACTION
}