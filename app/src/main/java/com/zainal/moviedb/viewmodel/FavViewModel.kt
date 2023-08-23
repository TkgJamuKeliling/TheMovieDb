package com.zainal.moviedb.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.db.MovieEntity
import com.zainal.moviedb.util.Repository
import kotlinx.coroutines.launch

class FavViewModel(private var repository: Repository) : BaseViewModel() {

    val movieEntity = MutableLiveData<List<MovieEntity>?>()
    fun vmMovieEntity(): LiveData<List<MovieEntity>?> = movieEntity

    init {
        viewModelScope.launch {
            repository.fetchFavData {
                movieEntity.postValue(it)
            }
        }
    }

}