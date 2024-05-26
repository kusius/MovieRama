package com.kusius.movies.database

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kusius.movies.database.dao.MovieDao
import com.kusius.movies.database.model.MovieGenreCrossRef
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MovieDetailsTest {
    private lateinit var movieDao: MovieDao
    private lateinit var db: MovieRamaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MovieRamaDatabase::class.java).build()
        movieDao = db.movieDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getsMovieDetails() = runTest {
        val movie = TestUtil.createMovie(1).first()
        val cast = TestUtil.createCastForMovie(castNumber = 3, movie = movie)
        val crew = TestUtil.createCrewForMovie(crewNumber = 3, movie = movie)
        val genres = TestUtil.createGenres(4)
        val movieWithGenres = genres.map {
            MovieGenreCrossRef(
                genreId = it.genreId,
                movieId = movie.movieId
            )
        }
        val reviews = TestUtil.createReviewsForMovie(reviewNumber = 10, movie = movie)

        db.withTransaction {
            movieDao.insertAll(listOf((movie)))
            movieDao.insertCast(cast)
            movieDao.insertCrew(crew)
            movieDao.insertGenres(genres)
            movieDao.associateMovieGenres(movieWithGenres)
            movieDao.insertReview(reviews)
        }

        val movieDetails = movieDao.getMovieDetails(movie.movieId)
        assertThat(movieDetails.movie, equalTo(movie))
        assertEquals(crew.size, movieDetails.crew.size)
        assertEquals(cast.size, movieDetails.cast.size)
        assertEquals(movieWithGenres.size, movieDetails.genres.size)
        assertEquals(reviews.size, movieDetails.reviews.size)
    }

    @Test
    fun movieDetailsNotAvailableShouldBeEmpty() = runTest {
        val movie = TestUtil.createMovie(1).first()

        movieDao.insertAll(listOf((movie)))

        val movieDetails = movieDao.getMovieDetails(movie.movieId)
        assertThat(movieDetails.movie, equalTo(movie))
        assertEquals(0, movieDetails.crew.size)
        assertEquals(0, movieDetails.cast.size)
        assertEquals(0, movieDetails.genres.size)
        assertEquals(0, movieDetails.reviews.size)
    }

}