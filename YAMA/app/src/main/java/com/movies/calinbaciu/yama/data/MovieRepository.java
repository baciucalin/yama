package com.movies.calinbaciu.yama.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.movies.calinbaciu.yama.data.local.Movie;
import com.movies.calinbaciu.yama.data.local.MoviesLocalDataSource;
import com.movies.calinbaciu.yama.data.local.RepositoryMovieResults;
import com.movies.calinbaciu.yama.data.remote.ApiResponseWrapper;
import com.movies.calinbaciu.yama.data.remote.MoviesRemoteDataSource;
import com.movies.calinbaciu.yama.data.remote.NetworkBoundResource;

import java.util.List;

import helpers.ExecutorHelper;
import helpers.MoviesFilterType;

/**
 * Repository pattern from the recommended app architecture implementation.
 * This repository depends on a persistent data model and a remote backend data source
 * It acts like a mediator between dirrerent data sources, and provides a single source of truth.
 */
public class MovieRepository implements DataSource {

    private static volatile MovieRepository INSTANCE;

    private final ExecutorHelper executorHelper;
    private final MoviesLocalDataSource localDataSource;
    private final MoviesRemoteDataSource remoteDataSource;

    public static MovieRepository getInstance(MoviesLocalDataSource localDataSource, MoviesRemoteDataSource remoteDataSource, ExecutorHelper executors) {
        if (INSTANCE == null) {
            synchronized (MovieRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MovieRepository(localDataSource, remoteDataSource, executors);
                }
            }
        }
        return INSTANCE;
    }

    private MovieRepository(MoviesLocalDataSource localDataSource, MoviesRemoteDataSource remoteDataSource, ExecutorHelper executors) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        executorHelper = executors;
    }


    @Override
    public RepositoryMovieResults loadMoviesFilteredBy(MoviesFilterType filterType, String searchTerm) {
        return remoteDataSource.loadMoviesFilteredBy(filterType, searchTerm);
    }



    @Override
    public LiveData<Resource<Movie>> loadMovie(long movieId) {
        return new NetworkBoundResource<Movie, Movie>(executorHelper) {
            @Override
            protected void saveCallResult(@NonNull Movie item) {
                localDataSource.saveMovie(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Movie data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<Movie> loadFromDb() {
                return localDataSource.getMovie(movieId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponseWrapper<Movie>> createCall() {
                return remoteDataSource.loadMovie(movieId);
            }

            @NonNull
            @Override
            protected void onFetchFailed() {
                Log.e(MovieRepository.class.getSimpleName(), "On fetch failed");
            }
        }.getAsLiveData();
    }

    public LiveData<List<Movie>> getAllMovies(){
        return localDataSource.getAllMovies();
    }

}
