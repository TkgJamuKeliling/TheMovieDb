package com.zainal.moviedb.helper

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zainal.moviedb.db.MovieDao
import com.zainal.moviedb.db.MovieEntity

class FavPagingSource(private var movieDao: MovieDao?): PagingSource<Int, MovieEntity>() {
    override fun getRefreshKey(state: PagingState<Int, MovieEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieEntity> {
        movieDao?.let {
            val page = params.key ?: 0

            return try {
                val entities = it.getAllData(params.loadSize, page * params.loadSize)
                LoadResult.Page(
                    data = entities,
                    prevKey = when (page) {
                        0 -> null
                        else -> page - 1
                    },
                    nextKey = when {
                        entities.isEmpty() -> null
                        else -> page + 1
                    }
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
        return LoadResult.Invalid()
    }
}