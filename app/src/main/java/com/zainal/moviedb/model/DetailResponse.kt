package com.zainal.moviedb.model

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("original_language")
	var originalLanguage: String? = "",

	@field:SerializedName("imdb_id")
	var imdbId: String? = "",

	@field:SerializedName("video")
	var video: Boolean = false,

	@field:SerializedName("title")
	var title: String? = "",

	@field:SerializedName("backdrop_path")
	var backdropPath: String? = "",

	@field:SerializedName("revenue")
	var revenue: Int = 0,

	@field:SerializedName("genres")
	var genres: List<DetailGenresItem?>? = emptyList(),

	@field:SerializedName("popularity")
	var popularity: Double? = 0.0,

	@field:SerializedName("production_countries")
	var productionCountries: List<ProductionCountriesItem?>? = emptyList(),

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("vote_count")
	var voteCount: Int = 0,

	@field:SerializedName("budget")
	var budget: Int = 0,

	@field:SerializedName("overview")
	var overview: String? = "",

	@field:SerializedName("original_title")
	var originalTitle: String? = "",

	@field:SerializedName("runtime")
	var runtime: Int = 0,

	@field:SerializedName("poster_path")
	var posterPath: String? = "",

	@field:SerializedName("spoken_languages")
	var spokenLanguages: List<SpokenLanguagesItem?>? = emptyList(),

	@field:SerializedName("production_companies")
	var productionCompanies: List<ProductionCompaniesItem?>? = emptyList(),

	@field:SerializedName("release_date")
	var releaseDate: String? = "",

	@field:SerializedName("vote_average")
	var voteAverage: Double? = 0.0,

	@field:SerializedName("belongs_to_collection")
	var belongsToCollection: BelongsCollection? = null,

	@field:SerializedName("tagline")
	var tagline: String? = "",

	@field:SerializedName("adult")
	var adult: Boolean = false,

	@field:SerializedName("homepage")
	var homepage: String? = "",

	@field:SerializedName("status")
	var status: String? = ""
)

data class SpokenLanguagesItem(

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("iso_639_1")
	var iso6391: String? = "",

	@field:SerializedName("english_name")
	var englishName: String? = ""
)

data class DetailGenresItem(

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("id")
	var id: Int = 0
)

data class ProductionCompaniesItem(

	@field:SerializedName("logo_path")
	var logoPath: String? = "",

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("origin_country")
	var originCountry: String? = ""
)

data class ProductionCountriesItem(

	@field:SerializedName("iso_3166_1")
	var iso31661: String? = "",

	@field:SerializedName("name")
	var name: String? = ""
)

data class BelongsCollection(

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("poster_path")
	var posterPath: String? = "",

	@field:SerializedName("backdrop_path")
	var backdropPath: String? = ""
)