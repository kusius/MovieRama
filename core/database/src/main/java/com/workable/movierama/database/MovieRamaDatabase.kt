package com.workable.movierama.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.workable.movierama.database.dao.MovieDao
import com.workable.movierama.database.dao.RemoteKeyDao
import com.workable.movierama.database.model.MovieEntity
import com.workable.movierama.database.model.RemoteKeyEntity
import org.koin.android.ext.koin.androidApplication

@Database(
    entities = [ MovieEntity::class, RemoteKeyEntity::class ],
    version = 1
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
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}