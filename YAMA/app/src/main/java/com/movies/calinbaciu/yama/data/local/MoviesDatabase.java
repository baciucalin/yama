package com.movies.calinbaciu.yama.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room database wrapper over the sqlite db.
 */

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {

    private static volatile MoviesDatabase INSTANCE;

    public abstract MovieDao movieDao();

    public static MoviesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MoviesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MoviesDatabase.class, "moviesDatabase.db").build();
                }
            }
        }
        return INSTANCE;
    }

}