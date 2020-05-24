package com.example.roadprotector.provider;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {RestStop.class}, version = 1)
public abstract class RestStopDatabase extends RoomDatabase {


    public static final String RESTSTOP_DATABASE_NAME = "reststop_database";

    public abstract RestStopDao restStopDao();

    // marking the instance as volatile to ensure atomic access to the variable
    // Rest of the threads are aware of any change made by any other thread
    private static volatile RestStopDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static RestStopDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RestStopDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RestStopDatabase.class, RESTSTOP_DATABASE_NAME).allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
