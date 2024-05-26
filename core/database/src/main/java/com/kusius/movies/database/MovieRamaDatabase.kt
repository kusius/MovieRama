package com.kusius.movies.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kusius.movies.database.dao.MovieDao
import com.kusius.movies.database.dao.RemoteKeyDao
import com.kusius.movies.database.model.CastEntity
import com.kusius.movies.database.model.CrewEntity
import com.kusius.movies.database.model.GenreEntity
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.MovieGenreCrossRef
import com.kusius.movies.database.model.RemoteKeyEntity

@Database(
    entities = [
        MovieEntity::class,
        RemoteKeyEntity::class,
        CastEntity::class,
        CrewEntity::class,
        GenreEntity::class,
        MovieGenreCrossRef::class
               ],
    version = 2
)
abstract class MovieRamaDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        private var INSTANCE: MovieRamaDatabase? = null

        fun getInstance(application: Application): MovieRamaDatabase {
            return INSTANCE ?: run {
                val instance = Room.databaseBuilder(
                    application,
                    MovieRamaDatabase::class.java,
                    "MovieRamaDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}