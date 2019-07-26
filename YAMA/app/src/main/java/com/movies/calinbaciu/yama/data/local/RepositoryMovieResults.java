package com.movies.calinbaciu.yama.data.local;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.movies.calinbaciu.yama.data.Resource;
import com.movies.calinbaciu.yama.data.remote.datapaging.MoviePagedKeyedDataSource;


public class RepositoryMovieResults {
    public LiveData<Resource> resource;
    public LiveData<PagedList<Movie>> data;
    public MutableLiveData<MoviePagedKeyedDataSource> sourceLiveData;

    public RepositoryMovieResults(LiveData<PagedList<Movie>> data, LiveData<Resource> resource, MutableLiveData<MoviePagedKeyedDataSource> sourceLiveData) {
        this.resource = resource;
        this.data = data;
        this.sourceLiveData = sourceLiveData;
    }
}
