package com.zainal.moviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zainal.moviedb.base.BaseViewModel
import com.zainal.moviedb.db.MovieEntity
import com.zainal.moviedb.helper.FavPagingSource
import com.zainal.moviedb.util.Repository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavViewModel(private var repository: Repository) : BaseViewModel() {

    private val movieEntity = MutableLiveData<PagingData<MovieEntity>?>()
    fun vmMovieEntity(): LiveData<PagingData<MovieEntity>?> = movieEntity

    val data = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 10
        )
    ) {
        FavPagingSource(repository.getMovieDao())
    }.flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            data.collectLatest {
                movieEntity.postValue(it)
            }
        }
    }

    fun removeItem(
        movieEntity: MovieEntity?,
        result: (Boolean) -> Unit
    ) {
        movieEntity?.let {
            viewModelScope.launch {
                repository.deleteFavItem(it)
                result(true)
            }
            return
        }
        result(false)
    }

    fun restoreItem(
        movieEntity: MovieEntity?,
        result: (Boolean) -> Unit
    ) {
        movieEntity?.let {
            viewModelScope.launch {
                repository.restoreFavItem(it)
                result(true)
            }
            return
        }
        result(false)
    }
}