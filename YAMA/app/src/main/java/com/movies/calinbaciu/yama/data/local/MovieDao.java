package com.movies.calinbaciu.yama.data.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("select * from movies where id = :movieId")
    LiveData<Movie> getMovieById(long movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Movie> movies);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

    @Update
    void update(Movie... movie);

}
