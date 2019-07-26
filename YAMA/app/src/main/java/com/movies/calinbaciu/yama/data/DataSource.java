package com.movies.calinbaciu.yama.data;

import androidx.lifecycle.LiveData;

import com.movies.calinbaciu.yama.data.local.Movie;
import com.movies.calinbaciu.yama.data.local.RepositoryMovieResults;

import java.util.List;

import helpers.MoviesFilterType;

/**
 * Contract for the repository pattern.
 * Can be mocked and used for testing.
 */

public interface DataSource {

        LiveData<Resource<Movie>> loadMovie(long movieId);

        RepositoryMovieResults loadMoviesFilteredBy(MoviesFilterType filterType, String searchTerm);

        LiveData<List<Movie>> getAllMovies();
}
