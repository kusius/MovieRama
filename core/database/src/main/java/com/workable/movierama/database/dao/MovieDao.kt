package com.workable.movierama.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.workable.movierama.database.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query(value = "SELECT * FROM movies")
    fun getMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query(value = "SELECT * FROM movies ORDER BY popularity DESC")
    fun pagingSourceByPopularity(): PagingSource<Int, MovieEntity>

    @Query(value = "SELECT * FROM movies WHERE isSearchResult = 1 ORDER BY popularity DESC")
    fun pagingSourceSearchResult(): PagingSource<Int, MovieEntity>

    @Query(value = "UPDATE movies SET isSearchResult = 0")
    suspend fun clearSearchResult()

    @Query(value = "SELECT * FROM movies where id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query(value = "UPDATE movies SET isFavourite = :isFavourite WHERE id = :movieId")
    fun updateMovieFavourite(movieId: Int, isFavourite: Boolean)

    @Query(value = "DELETE FROM movies")
    fun deleteAll()
}