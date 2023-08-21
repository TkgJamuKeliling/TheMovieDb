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
	var status: String? = "",

	//TV
	@field:SerializedName("created_by")
	var createBy: List<CreateBy>? = emptyList(),

	@field:SerializedName("episode_run_time")
	var episodeRunTime: List<Int>? = emptyList(),

	@field:SerializedName("first_air_date")
	var firstAirDate: String? = "",

	@field:SerializedName("in_production")
	var inProduction: Boolean = false,

	@field:SerializedName("languages")
	var languages: List<String>? = emptyList(),

	@field:SerializedName("last_air_date")
	var lastAirDate: String? = "",

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("type")
	var type: String? = "",

	@field:SerializedName("original_name")
	var originalName: String? = "",

	@field:SerializedName("number_of_episodes")
	var numberOfEpisodes: Int = 0,

	@field:SerializedName("number_of_seasons")
	var numberOfSeasons: Int = 0,

	@field:SerializedName("last_episode_to_air")
	var lastEpisodeToAir: LastEpisodeToAir? = null,

	@field:SerializedName("next_episode_to_air")
	var nextEpisodeToAir: NextEpisodeToAir? = null,

	@field:SerializedName("networks")
	var networks: List<Network>? = emptyList(),

	@field:SerializedName("origin_country")
	var originCountry: List<String>? = emptyList(),

	@field:SerializedName("seasons")
	var seasons: List<Seasons>? = emptyList(),
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

data class CreateBy(

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("credit_id")
	var creditId: String? = "",

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("gender")
	var gender: String? = "",

	@field:SerializedName("profile_path")
	var profilePath: String? = "",
)

data class LastEpisodeToAir(

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("runtime")
	var runtime: Int = 0,

	@field:SerializedName("season_number")
	var seasonNumber: Int = 0,

	@field:SerializedName("show_id")
	var showId: Int = 0,

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("still_path")
	var stillPath: String? = "",

	@field:SerializedName("overview")
	var overview: String? = "",

	@field:SerializedName("episode_type")
	var episodeType: String? = "",

	@field:SerializedName("production_code")
	var productionCode: String? = "",

	@field:SerializedName("vote_average")
	var voteAverage: Double? = 0.0,

	@field:SerializedName("vote_count")
	var voteCount: Int = 0,

	@field:SerializedName("episode_number")
	var episodeNumber: Int = 0,

	@field:SerializedName("air_date")
	var airDate: String? = ""
)

data class NextEpisodeToAir(

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("runtime")
	var runtime: Int = 0,

	@field:SerializedName("season_number")
	var seasonNumber: Int = 0,

	@field:SerializedName("show_id")
	var showId: Int = 0,

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("still_path")
	var stillPath: String? = "",

	@field:SerializedName("overview")
	var overview: String? = "",

	@field:SerializedName("episode_type")
	var episodeType: String? = "",

	@field:SerializedName("production_code")
	var productionCode: String? = "",

	@field:SerializedName("vote_average")
	var voteAverage: Double? = 0.0,

	@field:SerializedName("vote_count")
	var voteCount: Int = 0,

	@field:SerializedName("episode_number")
	var episodeNumber: Int = 0,

	@field:SerializedName("air_date")
	var airDate: String? = ""
)

data class Network(

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("logo_path")
	var logoPath: String? = "",

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("origin_country")
	var originCountry: String? = ""
)

data class Seasons(

	@field:SerializedName("air_date")
	var airDate: String? = "",

	@field:SerializedName("episode_count")
	var episodeCount: String? = "",

	@field:SerializedName("id")
	var id: Int = 0,

	@field:SerializedName("name")
	var name: String? = "",

	@field:SerializedName("overview")
	var overview: String? = "",

	@field:SerializedName("poster_path")
	var posterPath: String? = "",

	@field:SerializedName("season_number")
	var seasonNumber: Int = 0,

	@field:SerializedName("vote_average")
	var voteAverage: Double? = 0.0
)