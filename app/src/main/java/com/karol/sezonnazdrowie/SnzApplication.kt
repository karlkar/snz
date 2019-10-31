package com.karol.sezonnazdrowie

import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.karol.sezonnazdrowie.data.SnzDatabase
import com.karol.sezonnazdrowie.model.SnzAlarmManager

class SnzApplication : MultiDexApplication() {

    val database: SnzDatabase = SnzDatabase() // TODO Dagger

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        val lastAlarmSetTime = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getLong("pref_alarms_set", 0L)
        if (lastAlarmSetTime == 0L) {
            SnzAlarmManager.startSetAlarmsTask(applicationContext, database)
        }
    }
}
