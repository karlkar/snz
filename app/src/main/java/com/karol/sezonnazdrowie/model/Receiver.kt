package com.karol.sezonnazdrowie.model

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.view.MainActivity
import java.util.Calendar

private const val CHANNEL_ID = "SezonNaZdrowie"

class Receiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras ?: return

        val type = bundle.getString("type")
        val reqCode = bundle.getInt("reqCode")
        val title = bundle.getString("title")
        val text = bundle.getString("text")

        val notiIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.INTENT_WHAT, MainActivity.INTENT_WHAT_INCOMING)
        }
        val pIntent = TaskStackBuilder.create(context)
                .addParentStack(MainActivity::class.java)
                .addNextIntent(notiIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pIntent)
            .build()

        val notiMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notiMgr.notify(if (type == "start") 0 else 1, notification)

        val calendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, 1) // TODO: It is in order to display notification for next year...
            set(Calendar.HOUR, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
    }
}
