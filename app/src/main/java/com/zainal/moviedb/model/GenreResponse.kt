package com.zainal.moviedb.model

import com.google.gson.annotations.SerializedName
import com.zainal.moviedb.base.Equatable

data class GenreResponseModel(

	@field:SerializedName("genres")
	var genres: List<GenresItem?> = emptyList()
)

data class GenresItem(

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("id")
	var id: Int = 0,

	var icon: String? = ""
): Equatable
