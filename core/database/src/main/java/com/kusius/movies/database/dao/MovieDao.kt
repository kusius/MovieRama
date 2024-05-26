package com.kusius.movies.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.kusius.movies.database.model.CastEntity
import com.kusius.movies.database.model.CrewEntity
import com.kusius.movies.database.model.GenreEntity
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.MovieGenreCrossRef
import com.kusius.movies.database.model.relations.MovieDetailsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query(value = "SELECT * FROM movies")
    fun getMovies(): Flow<List<MovieEntity>>

    @Upsert
    suspend fun insertAll(movies: List<MovieEntity>)

    @Insert
    suspend fun insertCast(cast: List<CastEntity>)

    @Insert
    suspend fun insertCrew(crew: List<CrewEntity>)

    @Insert
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert
    suspend fun associateMovieGenres(movieAndGenres: List<MovieGenreCrossRef>)

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsRelation

    @Query("SELECT * FROM `cast` WHERE castMovieId = :movieId")
    suspend fun getCastByMovie(movieId: Int): List<CastEntity>

    @Query("SELECT * FROM `crew` WHERE crewMovieId = :movieId")
    suspend fun getCrewByMovie(movieId: Int): List<CrewEntity>

    @Query(value = "SELECT * FROM movies ORDER BY popularity DESC")
    fun pagingSourceByPopularity(): PagingSource<Int, MovieEntity>

    @Query(value = "SELECT * FROM movies WHERE isSearchResult = 1 ORDER BY popularity DESC")
    fun pagingSourceSearchResult(): PagingSource<Int, MovieEntity>

    @Query(value = "SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY popularity DESC")
    fun pagingSourceByQuery(query: String): PagingSource<Int, MovieEntity>

    @Query(value = "UPDATE movies SET isSearchResult = 0")
    suspend fun clearSearchResult()

    @Query(value = "SELECT * FROM movies where movieId = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query(value = "UPDATE movies SET isFavourite = :isFavourite WHERE movieId = :movieId")
    fun updateMovieFavourite(movieId: Int, isFavourite: Boolean)

    @Query(value = "DELETE FROM movies")
    fun deleteAll()
}