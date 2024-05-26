package com.kusius.movies.database.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.kusius.movies.database.model.CastEntity
import com.kusius.movies.database.model.CrewEntity
import com.kusius.movies.database.model.GenreEntity
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.MovieGenreCrossRef

data class MovieDetailsRelation(
    @Embedded val movie: MovieEntity,
    @Relation(
        entity = CastEntity::class,
        parentColumn = "movieId",
        entityColumn = "castMovieId"
    )
    val cast: List<CastEntity>,

    @Relation(
        entity = CrewEntity::class,
        parentColumn = "movieId",
        entityColumn = "crewMovieId"
    )
    val crew: List<CrewEntity>,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "genreId",
        associateBy = Junction(MovieGenreCrossRef::class)
    )
    val genres: List<GenreEntity>
)
