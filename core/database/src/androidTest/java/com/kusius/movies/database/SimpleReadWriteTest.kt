package com.kusius.movies.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kusius.movies.database.dao.MovieDao
import com.kusius.movies.database.model.MovieEntity
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
class SimpleReadWriteTest {
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
    fun writeMovieAndReadInList() = runTest {
        val movies: List<MovieEntity> = TestUtil.createMovie(3)

        movieDao.insertAll(movies)
        val byName = movieDao.getMovieById(0)
        assertThat(byName, equalTo(movies[0]))
    }
}