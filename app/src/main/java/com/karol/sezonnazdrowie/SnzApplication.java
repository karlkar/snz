package com.karol.sezonnazdrowie;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.karol.sezonnazdrowie.data.SnzDatabase;
import com.karol.sezonnazdrowie.model.SnzAlarmManager;

import java.util.concurrent.Executors;

public class SnzApplication extends MultiDexApplication {

    private SnzDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = Room
                .databaseBuilder(this, SnzDatabase.class, "snz.db")
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                mDatabase.populate();

                                boolean alarmsSet = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                        .getBoolean("pref_alarms_set", false);
                                if (!alarmsSet) {
                                    SnzAlarmManager.startSetAlarmsTask(getApplicationContext(), mDatabase);
                                }
                            }
                        });
                    }
                })
                .build();

        // In order to populate the database
        mDatabase.beginTransaction();
        mDatabase.endTransaction();
    }

    public SnzDatabase getDatabase() {
        return mDatabase;
    }
}
