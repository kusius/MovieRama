package com.kusius.movies.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kusius.movies.database.dao.MovieDao
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.ReviewEntity
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MovieReviewTest {
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
    @Throws(Exception::class)
    fun writeReviewMovieAndRead() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val reviews = TestUtil.createReviewsForMovie(movie = movies.first(), reviewNumber = 10)

        movieDao.insertAll(movies)
        val movieById = movieDao.getMovieById(0)
        MatcherAssert.assertThat(movieById, CoreMatchers.equalTo(movies[0]))

        movieDao.insertReview(reviews)
        val castById = movieDao.getReviewsByMovie(movies.first().movieId)
        TestCase.assertEquals(reviews.size, castById.size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun cannotWriteReviewForNonExistingMovie() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val reviews: List<ReviewEntity> = TestUtil.createReviewsForMovie(movies.first(), 3)
        movieDao.insertReview(reviews)
    }

    @Test
    fun reviewsGetsCascaded() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val reviews = TestUtil.createReviewsForMovie(movie = movies.first(), reviewNumber = 10)

        movieDao.insertAll(movies)
        movieDao.insertReview(reviews)

        val reviewByMovieId = movieDao.getReviewsByMovie(movies.first().movieId)
        TestCase.assertEquals(reviews.size, reviewByMovieId.size)

        movieDao.deleteAll()
        val reviewByMovieIdAfterDelete = movieDao.getCrewByMovie(movies.first().movieId)
        assertEquals(0, reviewByMovieIdAfterDelete.size)
    }
}