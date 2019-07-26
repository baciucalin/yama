package com.movies.calinbaciu.yama.data.remote;

import androidx.lifecycle.LiveData;

import com.movies.calinbaciu.yama.data.local.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface TmdbInteractor {
    @GET("movie/popular")
    Call<MovieApiResponse> getPopularMovies(@Query("page") int page);

    @GET("search/movie")
    Call<MovieApiResponse> searchMovies(
            @Query("query") String query,
            @Query("page") Integer page
//            @Query("language") String language,
//            @Query("include_adult") Boolean includeAdult,
//            @Query("year") Integer year,
//            @Query("primary_release_year") Integer primaryReleaseYear,
//            @Query("search_type") String searchType
    );

    @GET("movie/{id}")
    LiveData<ApiResponseWrapper<Movie>> getMovieDetails(@Path("id") long id);

}