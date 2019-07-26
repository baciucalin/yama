package com.movies.calinbaciu.yama.data.remote;

import com.google.gson.annotations.SerializedName;
import com.movies.calinbaciu.yama.data.local.Movie;

import java.util.List;

public class MovieApiResponse {

    @SerializedName("results")
    private List<Movie> movies;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("page")
    private int page;

    @SerializedName("total_pages")
    private int totalPages;

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

}
