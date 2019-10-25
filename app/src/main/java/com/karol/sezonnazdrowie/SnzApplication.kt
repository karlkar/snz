package com.karol.sezonnazdrowie

import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import com.karol.sezonnazdrowie.data.SnzDatabase
import com.karol.sezonnazdrowie.model.SnzAlarmManager

import java.util.concurrent.Executors

class SnzApplication : MultiDexApplication() {

    lateinit var database: SnzDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room
            .databaseBuilder<SnzDatabase>(this, SnzDatabase::class.java, "snz.db")
            .allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Executors.newSingleThreadExecutor().submit {
                        database.populate()

                        val alarmsSet =
                            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                                .getBoolean("pref_alarms_set", false)
                        if (!alarmsSet) {
                            SnzAlarmManager.startSetAlarmsTask(applicationContext, database)
                        }
                    }
                }
            })
            .build()

        // In order to populate the database
        database.beginTransaction()
        database.endTransaction()
    }
}
