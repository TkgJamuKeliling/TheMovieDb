package com.zainal.moviedb.model.response

import com.google.gson.annotations.SerializedName
import com.zainal.moviedb.base.Equatable

data class CastResponse(

    @field:SerializedName("cast")
	var cast: List<CastItem?>? = emptyList(),

    @field:SerializedName("id")
	var id: Int = 0,

    @field:SerializedName("crew")
	var crew: List<CrewItem?>? = emptyList()
)

data class CrewItem(

	@field:SerializedName("gender")
	var gender: Int = 0,

	@field:SerializedName("credit_id")
	var creditId: String? = "",

	@field:SerializedName("known_for_department")
	var knownForDepartment: String? = "",

	@field:SerializedName("original_name")
	var originalName: String? = "",

	@field:SerializedName("popularity")
	var popularity: Double? = 0.0,

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("profile_path")
	var profilePath: String? = "",

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("adult")
	var adult: Boolean = false,

	@field:SerializedName("department")
	var department: String? = "",

	@field:SerializedName("job")
	var job: String? = ""
)

data class CastItem(

	@field:SerializedName("cast_id")
	var castId: Int = 0,

	@field:SerializedName("character")
	var character: String? = "",

	@field:SerializedName("gender")
	var gender: Int = 0,

	@field:SerializedName("credit_id")
	var creditId: String? = "",

	@field:SerializedName("known_for_department")
	var knownForDepartment: String? = "",

	@field:SerializedName("original_name")
	var originalName: String? = "",

	@field:SerializedName("popularity")
	var popularity: Double? = 0.0,

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("profile_path")
	var profilePath: String? = "",

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("adult")
	var adult: Boolean = false,

	@field:SerializedName("order")
	var order: Int = 0
): Equatable
