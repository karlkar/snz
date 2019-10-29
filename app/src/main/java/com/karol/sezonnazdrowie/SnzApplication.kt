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

        val alarmsSet = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getBoolean("pref_alarms_set", false)
        if (!alarmsSet) {
            SnzAlarmManager.startSetAlarmsTask(applicationContext, database)
        }
    }
}
