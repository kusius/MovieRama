package com.kusius.movies.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kusius.movies.database.dao.MovieDao
import com.kusius.movies.database.model.CastEntity
import com.kusius.movies.database.model.CrewEntity
import com.kusius.movies.database.model.MovieEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MovieCastAndCrewTest {
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
    fun writeCastForMovieAndRead() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val cast: List<CastEntity> = TestUtil.createCastForMovie(movies.first(), 3)

        movieDao.insertAll(movies)
        val movieById = movieDao.getMovieById(0)
        assertThat(movieById, CoreMatchers.equalTo(movies[0]))

        movieDao.insertCast(cast)
        val castById = movieDao.getCastByMovie(movies.first().movieId)
        assertEquals(cast.size, castById.size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun cannotWriteCastForNonExistingMovie() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val cast: List<CastEntity> = TestUtil.createCastForMovie(movies.first(), 3)
        movieDao.insertCast(cast)
    }

    @Test
    @Throws(Exception::class)
    fun writeCrewForMovieAndRead() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val crew: List<CrewEntity> = TestUtil.createCrewForMovie(movies.first(), 3)

        movieDao.insertAll(movies)
        val movieById = movieDao.getMovieById(0)
        assertThat(movieById, CoreMatchers.equalTo(movies[0]))

        movieDao.insertCrew(crew)
        val crewByMovieId = movieDao.getCrewByMovie(movies.first().movieId)
        assertEquals(crew.size, crewByMovieId.size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun cannotWriteCrewForNonExistingMovie() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val crew: List<CrewEntity> = TestUtil.createCrewForMovie(movies.first(), 3)
        movieDao.insertCrew(crew)
    }

    @Test
    fun crewAndCastGetsCascaded() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(1)
        val crew: List<CrewEntity> = TestUtil.createCrewForMovie(movies.first(), 3)
        val cast: List<CastEntity> = TestUtil.createCastForMovie(movies.first(), 3)

        movieDao.insertAll(movies)
        movieDao.insertCrew(crew)
        movieDao.insertCast(cast)

        val crewByMovieId = movieDao.getCrewByMovie(movies.first().movieId)
        val castByMovieId = movieDao.getCastByMovie(movies.first().movieId)
        assertEquals(crew.size, crewByMovieId.size)
        assertEquals(cast.size, castByMovieId.size)

        movieDao.deleteAll()
        val crewByMovieIdAfterDelete = movieDao.getCrewByMovie(movies.first().movieId)
        val castByMovieIdAfterDelete = movieDao.getCastByMovie(movies.first().movieId)
        assertEquals(0, crewByMovieIdAfterDelete.size)
        assertEquals(0, castByMovieIdAfterDelete.size)
    }
}