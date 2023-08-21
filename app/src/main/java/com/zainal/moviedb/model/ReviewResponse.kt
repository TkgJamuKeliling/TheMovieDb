package com.zainal.moviedb.model

import com.google.gson.annotations.SerializedName
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.util.ReviewSection

data class ReviewResponse(

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("page")
	var page: Int = 1,

	@field:SerializedName("total_pages")
	var totalPages: Int = 0,

	@field:SerializedName("results")
	var results: List<ReviewResultsItem?>? = emptyList(),

	@field:SerializedName("total_results")
	var totalResults: Int = 0
)

data class ReviewResultsItem(
	var reviewSection: Int = ReviewSection.DATA.type,

	@field:SerializedName("author_details")
	var authorDetails: AuthorDetails? = null,

	@field:SerializedName("updated_at")
	var updatedAt: String? = "",

	@field:SerializedName("author")
	var author: String? = "",

	@field:SerializedName("created_at")
	var createdAt: String? = "",

	@field:SerializedName("id")
	var id: String? = "",

	@field:SerializedName("content")
	var content: String? = "",

	@field:SerializedName("url")
	var url: String? = ""
): Equatable

data class AuthorDetails(

	@field:SerializedName("avatar_path")
	var avatarPath: String? = "",

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("rating")
	var rating: String? = "",

	@field:SerializedName("username")
	var username: String? = ""
)
