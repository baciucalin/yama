package com.movies.calinbaciu.yama.data.remote;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.movies.calinbaciu.yama.data.Resource;
import com.movies.calinbaciu.yama.data.local.Movie;
import com.movies.calinbaciu.yama.data.local.RepositoryMovieResults;
import com.movies.calinbaciu.yama.data.remote.datapaging.MoviePagedDataSourceFactory;
import com.movies.calinbaciu.yama.data.remote.datapaging.MoviePagedKeyedDataSource;

import helpers.ExecutorHelper;
import helpers.MoviesFilterType;

public class MoviesRemoteDataSource {

    private static volatile MoviesRemoteDataSource INSTANCE;

    private static final int PAGE_SIZE = 20;
    private final ExecutorHelper mExecutors;
    private final TmdbInteractor mTmdbInteractor;

    private MoviesRemoteDataSource(TmdbInteractor tmdbInteractor, ExecutorHelper executors) {
        mTmdbInteractor = tmdbInteractor;
        mExecutors = executors;
    }

    public static MoviesRemoteDataSource getInstance(TmdbInteractor tmdbInteractor, ExecutorHelper executors) {
        if (INSTANCE == null) {
            synchronized (ExecutorHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MoviesRemoteDataSource(tmdbInteractor, executors);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<ApiResponseWrapper<Movie>> loadMovie(final long movieId) {
        return mTmdbInteractor.getMovieDetails(movieId);
    }

    public RepositoryMovieResults loadMoviesFilteredBy(MoviesFilterType filterType, String searchTerm) {
        MoviePagedDataSourceFactory sourceFactory = new MoviePagedDataSourceFactory(mTmdbInteractor, filterType, searchTerm);

        PagedList.Config pageConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(PAGE_SIZE)
                .build();


        LiveData<PagedList<Movie>> moviesPagedList = new LivePagedListBuilder<>(sourceFactory, pageConfig)
                .setFetchExecutor(mExecutors.networkIO())
                .build();

        LiveData<Resource> networkState = Transformations.switchMap(sourceFactory.sourceLiveData, new Function<MoviePagedKeyedDataSource, LiveData<Resource>>() {
            @Override
            public LiveData<Resource> apply(MoviePagedKeyedDataSource input) {
                return input.movieResults;
            }
        });

        // The pagedList and network errors are exposed to VM
        return new RepositoryMovieResults(moviesPagedList, networkState, sourceFactory.sourceLiveData);
    }

}
