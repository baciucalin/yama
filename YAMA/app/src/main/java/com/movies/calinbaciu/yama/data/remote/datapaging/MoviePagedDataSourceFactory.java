package com.movies.calinbaciu.yama.data.remote.datapaging;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.movies.calinbaciu.yama.data.local.Movie;
import com.movies.calinbaciu.yama.data.remote.TmdbInteractor;

import java.util.concurrent.Executor;

import helpers.MoviesFilterType;

public class MoviePagedDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    public MutableLiveData<MoviePagedKeyedDataSource> sourceLiveData = new MutableLiveData<>();

    private final TmdbInteractor tmdbInteractor;
    private final MoviesFilterType filterType;
    private final String searchTerm;

    public MoviePagedDataSourceFactory(TmdbInteractor tmdbInteractor, MoviesFilterType filterType, String searchTerm) {
        this.tmdbInteractor = tmdbInteractor;
        this.filterType = filterType;
        this.searchTerm = searchTerm;
    }

    @Override
    public DataSource<Integer, Movie> create() {
        MoviePagedKeyedDataSource movieDataSource = new MoviePagedKeyedDataSource(tmdbInteractor, filterType, searchTerm);
        sourceLiveData.postValue(movieDataSource);
        return movieDataSource;
    }
}
