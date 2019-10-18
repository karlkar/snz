package com.karol.sezonnazdrowie;

import android.app.Application;

import com.karol.sezonnazdrowie.data.Database;

import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class SnzApplication extends Application {

    private Database mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = Room
                .databaseBuilder(this, Database.class, "snz.db")
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                mDatabase.populate();
                            }
                        });
                    }
                })
                .build();
    }

    public Database getDatabase() {
        return mDatabase;
    }
}
