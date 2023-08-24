package com.zainal.moviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.model.response.GenreResponse
import com.zainal.moviedb.model.response.GenresItem
import com.zainal.moviedb.model.response.TrendingResultsItem
import com.zainal.moviedb.util.Repository

open class MainFragmentViewModel(var repository: Repository) : BaseViewModel() {

    val trendingResultsItem = MutableLiveData<List<TrendingResultsItem>?>()
    fun vmTrendingResultsItem(): LiveData<List<TrendingResultsItem>?> = trendingResultsItem

    val genresItem = MutableLiveData<List<GenresItem>?>()
    fun vmGenresItem(): LiveData<List<GenresItem>?> = genresItem

    val genreResponse = MutableLiveData<GenreResponse?>()
}