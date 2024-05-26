package com.kusius.movies.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.kusius.movies.core.model.MovieReview

@Entity(
    tableName = "review",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = arrayOf("movieId"),
            childColumns = arrayOf("reviewMovieId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val reviewId: Int = 0,
    val reviewMovieId: Int,
    val author: String,
    val content: String
)

fun ReviewEntity.asExternalModel() = MovieReview(
    authorName = this.author,
    content = this.content
)