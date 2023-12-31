package com.zainal.moviedb.model.response

import com.google.gson.annotations.SerializedName
import com.zainal.moviedb.base.Equatable

data class TrendingResponse(

    @field:SerializedName("page")
	var page: Int = 1,

    @field:SerializedName("total_pages")
	var totalPages: Int = 0,

    @field:SerializedName("results")
	var results: List<TrendingResultsItem?> = emptyList(),

    @field:SerializedName("total_results")
	var totalResults: Int = 0
)

data class TrendingResultsItem(

	@field:SerializedName("overview")
	var overview: String = "",

	@field:SerializedName("original_language")
	var originalLanguage: String = "",

	@field:SerializedName("original_title")
	var originalTitle: String = "",

	@field:SerializedName("video")
	var video: Boolean = false,

	@field:SerializedName("title")
	var title: String = "",

	@field:SerializedName("genre_ids")
	var genreIds: List<Int> = emptyList(),

	@field:SerializedName("poster_path")
	var posterPath: String = "",

	@field:SerializedName("backdrop_path")
	var backdropPath: String = "",

	@field:SerializedName("media_type")
	var mediaType: String = "",

	@field:SerializedName("release_date")
	var releaseDate: String = "",

	@field:SerializedName("popularity")
	var popularity: Double? = 0.0,

	@field:SerializedName("vote_average")
	var voteAverage: Double? = 0.0,

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("adult")
	var adult: Boolean = false,

	@field:SerializedName("vote_count")
	var voteCount: Int = 0,

	//TV Show
	@field:SerializedName("original_name")
	var originalName: String = "",

	@field:SerializedName("name")
	var name: String = "",

	@field:SerializedName("first_air_date")
	var firstAirDate: String = "",

	@field:SerializedName("origin_country")
	var originCountry: List<String> = emptyList(),
): Equatable
