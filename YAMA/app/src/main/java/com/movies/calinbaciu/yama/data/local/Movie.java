package com.movies.calinbaciu.yama.data.local;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This is the Model in the MVVM pattern.
 */

@Entity(tableName = "movies")
public class Movie implements Serializable {

    @PrimaryKey
    @NonNull
    private int id;

//    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private double voteAverage;

//    @ColumnInfo(name = "vote_count")
    @SerializedName("vote_count")
    private int voteCount;

//    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

//    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @SerializedName("title")
    private String title;

//    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private String posterPath;

//    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("original_title")
    private String originalTitle;


    @Ignore
    public Movie(int id, String originalTitle, String overview, String releaseDate, String posterPath) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }

    public Movie(int id, double voteAverage, int voteCount, String originalTitle, String title, double popularity, String backdropPath, String overview, String releaseDate, String posterPath) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.originalTitle = originalTitle;
        this.title = title;
        this.popularity = popularity;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


    public static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Movie article = (Movie) obj;
        return article.id == this.id;
    }


}
