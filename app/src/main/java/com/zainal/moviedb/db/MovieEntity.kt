package com.zainal.moviedb.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favTable")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "movieId")
    var movieId: Int,

    @ColumnInfo(name = "movieDetail")
    var movieDetail: String,

    @ColumnInfo(name = "typeCategory")
    var typeCategory: String
)