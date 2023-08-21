package com.zainal.moviedb.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [MovieEntity::class], exportSchema = false)
abstract class MovieDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        var INSTANCE: MovieDatabase? = null

        @Synchronized
        fun getInstance(context: Context?): MovieDatabase? {
            INSTANCE?.let {
                return it
            }

            INSTANCE = context?.let {
                Room.databaseBuilder(
                    it,
                    MovieDatabase::class.java,
                    "movieFav"
                ).build()
            }
            return INSTANCE
        }
    }
}