package com.zainal.moviedb.model

import com.google.gson.annotations.SerializedName
import com.zainal.moviedb.base.Equatable

data class VideoResponse(

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("results")
	var results: List<VideoResultsItem?>? = emptyList()
)

data class VideoResultsItem(

	@field:SerializedName("site")
	var site: String? = "",

	@field:SerializedName("size")
	var size: Int = 0,

	@field:SerializedName("iso_3166_1")
	var iso31661: String? = "",

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("official")
	var official: Boolean = false,

	@field:SerializedName("id")
	var id: String? = "",

	@field:SerializedName("type")
	var type: String? = "",

	@field:SerializedName("published_at")
	var publishedAt: String? = "",

	@field:SerializedName("iso_639_1")
	var iso6391: String? = "",

	@field:SerializedName("key")
	var key: String? = ""
): Equatable
