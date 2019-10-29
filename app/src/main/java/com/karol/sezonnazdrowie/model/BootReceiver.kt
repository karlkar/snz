package com.karol.sezonnazdrowie.model

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.karol.sezonnazdrowie.SnzApplication

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val currentDayProvider = SnzAlarmManager.TimeDataProviderImpl()
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            SnzAlarmManager.setAlarms(
                context,
                currentDayProvider,
                sharedPreferences,
                alarmManager,
                (context.applicationContext as SnzApplication).database
            )
        }
    }
}
