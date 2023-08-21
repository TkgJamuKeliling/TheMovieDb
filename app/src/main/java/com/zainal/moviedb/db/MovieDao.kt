package com.zainal.moviedb.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("SELECT * FROM favTable WHERE movieId = :id LIMIT 1")
    suspend fun isFavExist(id: String): MovieEntity?

    @Delete(entity = MovieEntity::class)
    suspend fun deleteFav(entity: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFav(entity: MovieEntity)
}