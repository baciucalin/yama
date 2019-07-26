package com.movies.calinbaciu.yama.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;

import com.movies.calinbaciu.yama.R;
import com.movies.calinbaciu.yama.data.MovieRepository;
import com.movies.calinbaciu.yama.data.local.Movie;
import com.movies.calinbaciu.yama.data.local.MoviesDatabase;
import com.movies.calinbaciu.yama.data.local.MoviesLocalDataSource;
import com.movies.calinbaciu.yama.data.remote.MovieApiClient;
import com.movies.calinbaciu.yama.data.remote.TmdbInteractor;
import com.movies.calinbaciu.yama.data.remote.MoviesRemoteDataSource;
import com.movies.calinbaciu.yama.ui.SearchMoviesViewModel;

import java.util.ArrayList;
import java.util.List;

import helpers.ExecutorHelper;
import helpers.MoviesFilterType;

/**
 * This is the View in the MVVM pattern. It only contains UI logic and
 * has a reference to the ViewModel, the next component of the MVVM.
 */

public class MainActivity extends AppCompatActivity {

    private SearchMoviesViewModel searchMoviesViewModel;
    ExecutorHelper executors = ExecutorHelper.getInstance();

    private List<String> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchView searchView = findViewById(R.id.movie_sv);
        ListView listView = findViewById(R.id.movie_list_lv);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, movieList);
        listView.setAdapter(listAdapter);


        MovieRepository repository = MovieRepository.getInstance(getLocalDataSource(), getRemoteDataSource(), executors);
        searchMoviesViewModel = new SearchMoviesViewModel(repository);


        searchMoviesViewModel.getMoviePagedList().observe(this, new Observer<PagedList<Movie>>() {
            @Override
            public void onChanged(PagedList<Movie> movies) {
                movieList.clear();
                for (Movie m : movies) {
                    movieList.add(m.getTitle()+ " avg.: " + m.getVoteAverage());
                    Log.d(MainActivity.class.getSimpleName(), "movie: " + m.getTitle());
                }
                listAdapter.notifyDataSetChanged();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchTerm) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchTerm) {
                if(searchTerm.isEmpty()){
                    searchMoviesViewModel.setFilterType(MoviesFilterType.TOP_RATED);
                }else{
                    getAllMoviesByName(searchTerm);
                    searchMoviesViewModel.setFilterType(MoviesFilterType.SEARCH_RESULTS);
                }
                return true;
            }
        });

    }

    private TmdbInteractor getRemoteAPI(){
        return MovieApiClient.getInstance();
    }

    private MoviesRemoteDataSource getRemoteDataSource() {
        return MoviesRemoteDataSource.getInstance(getRemoteAPI(), executors);
    }

    private MoviesLocalDataSource getLocalDataSource() {
        return MoviesLocalDataSource.getInstance(MoviesDatabase.getInstance(getApplicationContext()));
    }

    private void getAllMoviesByName(String searchTerm){
        searchMoviesViewModel.setSearchTerm(searchTerm);
    }

}
