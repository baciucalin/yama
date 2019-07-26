package com.movies.calinbaciu.yama.data.remote.datapaging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.movies.calinbaciu.yama.data.Resource;
import com.movies.calinbaciu.yama.data.local.Movie;
import com.movies.calinbaciu.yama.data.remote.MovieApiResponse;
import com.movies.calinbaciu.yama.data.remote.TmdbInteractor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import helpers.MoviesFilterType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Incremental data loader for page-keyed content, where requests return keys for next/previous pages.
 *
 * Implement a DataSource using PageKeyedDataSource if you need to use data from page N - 1 to load page N.
 *
 * This is common, for example, in network APIs that include a next/previous link or key with each page load.
 */

public class MoviePagedKeyedDataSource extends PageKeyedDataSource<Integer, Movie> {

    private static final int FIRST_PAGE = 1;

    public MutableLiveData<Resource> movieResults = new MutableLiveData<>();

    private final TmdbInteractor tmdbInteractor;
    private final MoviesFilterType filterType;
    private final String searchTerm;


    public MoviePagedKeyedDataSource(TmdbInteractor tmdbInteractor, MoviesFilterType filterType, String searchTerm) {
        this.tmdbInteractor = tmdbInteractor;
        this.filterType = filterType;
        this.searchTerm = searchTerm;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Movie> callback) {
        movieResults.postValue(Resource.loading(null));

        Call<MovieApiResponse> request;
        if (filterType == MoviesFilterType.TOP_RATED) {
            request = tmdbInteractor.getPopularMovies(FIRST_PAGE);
        } else {
            request = tmdbInteractor.searchMovies(searchTerm, FIRST_PAGE);
        }

        try {
            Response<MovieApiResponse> response = request.execute();
            MovieApiResponse data = response.body();
            List<Movie> movieList = data != null ? data.getMovies() : Collections.emptyList();

            movieResults.postValue(Resource.success(null));
            callback.onResult(movieList, null, FIRST_PAGE + 1);
        } catch (IOException e) {
            movieResults.postValue(Resource.error(e.getMessage(), null));
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {
        movieResults.postValue(Resource.loading(null));

        // load data from API
        Call<MovieApiResponse> request;
        if (filterType == MoviesFilterType.TOP_RATED) {
            request = tmdbInteractor.getPopularMovies(params.key);
        } else {
            request = tmdbInteractor.searchMovies(searchTerm, params.key);
        }

        request.enqueue(new Callback<MovieApiResponse>() {
            @Override
            public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response) {
                if (response.isSuccessful()) {
                    MovieApiResponse data = response.body();
                    List<Movie> movieList = data != null ? data.getMovies() : Collections.emptyList();

                    callback.onResult(movieList, params.key + 1);
                    movieResults.postValue(Resource.success(null));
                } else {
                    movieResults.postValue(Resource.error("error code: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<MovieApiResponse> call, Throwable t) {
                movieResults.postValue(Resource.error(t != null ? t.getMessage() : "unknown error", null));
            }
        });
    }

}
