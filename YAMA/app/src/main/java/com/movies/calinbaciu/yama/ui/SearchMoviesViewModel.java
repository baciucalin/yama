package com.movies.calinbaciu.yama.ui;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

import com.movies.calinbaciu.yama.data.MovieRepository;
import com.movies.calinbaciu.yama.data.Resource;
import com.movies.calinbaciu.yama.data.local.Movie;
import com.movies.calinbaciu.yama.data.local.RepositoryMovieResults;

import helpers.MoviesFilterType;

/**
 * This is the ViewModel in the MVVM pattern. It's role is to catch events from the View and to interact with the model.
 * The ViewModel should know nothing about the View from where it is called.
 * https://developer.android.com/topic/libraries/architecture/viewmodel
 */


public class SearchMoviesViewModel {
    private LiveData<Resource> networkState;
    private LiveData<PagedList<Movie>> moviePagedList;

    private MutableLiveData<MoviesFilterType> filterType = new MutableLiveData<>();
    private String searchTerm;

    public SearchMoviesViewModel(final MovieRepository movieRepository) {
        filterType.setValue(MoviesFilterType.TOP_RATED);

        LiveData<RepositoryMovieResults> repositoryMovieResults = Transformations.map(filterType, new Function<MoviesFilterType, RepositoryMovieResults>() {
            @Override
            public RepositoryMovieResults apply(MoviesFilterType filterType) {
                return movieRepository.loadMoviesFilteredBy(filterType, searchTerm);
            }
        });

        moviePagedList = Transformations.switchMap(repositoryMovieResults,
                new Function<RepositoryMovieResults, LiveData<PagedList<Movie>>>() {
                    @Override
                    public LiveData<PagedList<Movie>> apply(RepositoryMovieResults input) {
                        return input.data;
                    }
                });

        networkState = Transformations.switchMap(repositoryMovieResults, new Function<RepositoryMovieResults, LiveData<Resource>>() {
            @Override
            public LiveData<Resource> apply(RepositoryMovieResults input) {
                return input.resource;
            }
        });
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public void setFilterType(MoviesFilterType type){
        filterType.setValue(type);
    }

    public LiveData<PagedList<Movie>> getMoviePagedList() {
        return moviePagedList;
    }

    public LiveData<Resource> getNetworkState() {
        return networkState;
    }

    public MoviesFilterType getCurrentFilterType() {
        return filterType.getValue();
    }

}
