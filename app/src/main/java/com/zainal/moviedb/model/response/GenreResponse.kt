package com.zainal.moviedb.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.zainal.moviedb.base.Equatable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenreResponse(

	@field:SerializedName("genres")
	var genres: List<GenresItem?> = emptyList()
): Parcelable

@Parcelize
data class GenresItem(

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("id")
	var id: Int = 0,

	var icon: String? = ""
): Equatable, Parcelable
