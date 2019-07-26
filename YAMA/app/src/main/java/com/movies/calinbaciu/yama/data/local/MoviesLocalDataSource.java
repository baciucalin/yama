package com.movies.calinbaciu.yama.data.local;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import helpers.ExecutorHelper;

/**
 * The local data component of the repository.
 */

public class MoviesLocalDataSource {
    private static volatile MoviesLocalDataSource INSTANCE;
    private final MoviesDatabase mDatabase;

    private MoviesLocalDataSource(MoviesDatabase database) {
        mDatabase = database;
    }

    public static MoviesLocalDataSource getInstance(MoviesDatabase database) {
        if (INSTANCE == null) {
            synchronized (ExecutorHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MoviesLocalDataSource(database);
                }
            }
        }
        return INSTANCE;
    }


    public LiveData<List<Movie>> getAllMovies() {
        return mDatabase.movieDao().getAllMovies();
    }

    public LiveData<Movie> getMovie(long movieId) {
        Log.d(MoviesLocalDataSource.class.getSimpleName(), "Loading movie details from db.");
        return mDatabase.movieDao().getMovieById(movieId);
    }

    public void saveMovie(Movie movie) {
        mDatabase.movieDao().insertMovie(movie);
    }

}
