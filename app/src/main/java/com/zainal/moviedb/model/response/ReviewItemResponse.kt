package com.zainal.moviedb.model.response

import com.google.gson.annotations.SerializedName

data class ReviewItemResponse(

    @field:SerializedName("media_title")
	var mediaTitle: String? = "",

    @field:SerializedName("author_details")
	var authorDetails: ReviewItemAuthorDetails? = null,

    @field:SerializedName("updated_at")
	var updatedAt: String? = "",

    @field:SerializedName("media_type")
	var mediaType: String? = "",

    @field:SerializedName("author")
	var author: String? = "",

    @field:SerializedName("created_at")
	var createdAt: String? = "",

    @field:SerializedName("media_id")
	var mediaId: Int = 0,

    @field:SerializedName("id")
	var id: String? = "",

    @field:SerializedName("iso_639_1")
	var iso6391: String? = "",

    @field:SerializedName("content")
	var content: String? = "",

    @field:SerializedName("url")
	var url: String? = ""
)

data class ReviewItemAuthorDetails(

	@field:SerializedName("avatar_path")
	var avatarPath: String? = "",

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("rating")
	var rating: String? = "",

	@field:SerializedName("username")
	var username: String? = ""
)
