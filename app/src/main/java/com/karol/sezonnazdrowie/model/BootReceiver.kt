package com.karol.sezonnazdrowie.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.karol.sezonnazdrowie.SnzApplication

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            SnzAlarmManager.setAlarms(
                context,
                (context.applicationContext as SnzApplication).database
            )
        }
    }
}
