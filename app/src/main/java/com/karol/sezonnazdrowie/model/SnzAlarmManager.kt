package com.karol.sezonnazdrowie.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.data.SnzDatabase
import com.karol.sezonnazdrowie.view.controls.TimePreference
import org.threeten.bp.LocalDate
import org.threeten.bp.MonthDay
import org.threeten.bp.OffsetDateTime
import java.util.concurrent.Executors

object SnzAlarmManager {

    private const val TAG = "SNZALARMMANAGER"

    fun startSetAlarmsTask(ctx: Context, database: SnzDatabase) {
        Executors.newSingleThreadExecutor().submit {
            setAlarms(ctx, database)
        }
    }

    internal fun setAlarms(ctx: Context, database: SnzDatabase) {
        val allItems = database.allFruits + database.allVegetables
        val startMap = groupByStartDates(allItems)
        val endMap = groupByEndDates(allItems)

        val zoneOffset = OffsetDateTime.now().offset

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

        val startDays = mutableListOf<Int>()
        val seasonStart = sharedPreferences.getStringSet("pref_season_start", null)
        if (seasonStart == null) {
            startDays.add(7)
        } else {
            if (seasonStart.contains(ctx.getString(R.string.at_the_start_day))) {
                startDays.add(0)
            }
            if (seasonStart.contains(ctx.getString(R.string.week_before))) {
                startDays.add(7)
            }
            if (seasonStart.contains(ctx.getString(R.string.month_before))) {
                startDays.add(30)
            }
        }

        var intent: Intent
        var alarmIntent: PendingIntent
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var reqCode = 1
        val notiTime = sharedPreferences.getString("pref_notification_hour", "20:00")
        val notiHour = TimePreference.getHour(notiTime!!)
        val notiMinute = TimePreference.getMinute(notiTime)

        val today = LocalDate.now()
        startMap.entries.forEach {
            val strBuilder = StringBuilder()
            for (item in it.value) {
                if (sharedPreferences.getBoolean("pref_noti_${item.name}", true)) {
                    if (strBuilder.isNotEmpty()) {
                        strBuilder.append(", ")
                    }
                    strBuilder.append(item.conjugatedName)
                }
            }
            if (strBuilder.isNotEmpty()) {
                for (dayDiff in startDays) {
                    val title = when (dayDiff) {
                        0 -> ctx.getString(R.string.season_starts_soon)
                        7 -> ctx.getString(R.string.season_starts_week)
                        30 -> ctx.getString(R.string.season_starts_month)
                        else -> ""
                    }
                    intent = Intent(ctx, Receiver::class.java).apply {
                        putExtra("type", "start")
                        putExtra("reqCode", reqCode)
                        putExtra("title", title)
                        putExtra("text", strBuilder.toString())
                    }
                    alarmIntent = PendingIntent.getBroadcast(
                        ctx,
                        reqCode++,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val nextNotificationDate = it.key.atYear(today.year)
                    with(nextNotificationDate) {
                        minusDays(dayDiff.toLong())
                        if (isBefore(today)) {
                            plusYears(1)
                        }
                    }
                    val notificationTime = nextNotificationDate.atTime(notiHour, notiMinute)

                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime.toInstant(zoneOffset).toEpochMilli(),
                        alarmIntent
                    )
                    Log.d(
                        TAG,
                        "setAlarms: Start Alarm set @ ${nextNotificationDate.dayOfMonth}.${nextNotificationDate.month}"
                    )
                }
            }
        }

        val endDays = mutableListOf<Int>()
        val seasonEnd = sharedPreferences.getStringSet("pref_season_end", null)
        if (seasonEnd == null) {
            endDays.add(7)
        } else {
            if (seasonEnd.contains(ctx.getString(R.string.at_the_end_day))) {
                endDays.add(0)
            }
            if (seasonEnd.contains(ctx.getString(R.string.week_before))) {
                endDays.add(7)
            }
        }

        endMap.entries.forEach {
            val strBuilder = StringBuilder()
            for (item in it.value) {
                if (sharedPreferences.getBoolean("pref_noti_${item.name}", true)) {
                    if (strBuilder.isNotEmpty()) {
                        strBuilder.append(", ")
                    }
                    strBuilder.append(item.conjugatedName)
                } else {
                    Log.d(TAG, "setAlarms: Skipping the item ${item.name}")
                }
            }
            if (strBuilder.isNotEmpty()) {
                for (dayDiff in endDays) {
                    val title = when (dayDiff) {
                        0 -> ctx.getString(R.string.season_ends_soon)
                        7 -> ctx.getString(R.string.season_ends_week)
                        else -> ""
                    }
                    intent = Intent(ctx, Receiver::class.java).apply {
                        putExtra("type", "end")
                        putExtra("reqCode", reqCode)
                        putExtra("title", title)
                        putExtra("text", strBuilder.toString())
                    }
                    alarmIntent = PendingIntent.getBroadcast(
                        ctx,
                        reqCode++,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val nextNotificationDate = it.key.atYear(today.year)
                    with(nextNotificationDate) {
                        minusDays(dayDiff.toLong())
                        if (isBefore(today)) {
                            plusYears(1)
                        }
                    }
                    val nextNotificationTime = nextNotificationDate
                        .atTime(notiHour, notiMinute)

                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        nextNotificationTime.toInstant(zoneOffset).toEpochMilli(),
                        alarmIntent
                    )
                    Log.d(
                        TAG,
                        "setAlarms: End Alarm set @ ${nextNotificationDate.dayOfMonth}.${nextNotificationDate.month}"
                    )
                }
            }
        }
        val prevMaxReqCode = sharedPreferences.getInt("maxReqCode", 0)
        Log.d(TAG, "setAlarms: Ending with $reqCode, previous ending $prevMaxReqCode")
        sharedPreferences.edit().putInt("maxReqCode", reqCode).apply()

        intent = Intent(ctx, Receiver::class.java)
        while (prevMaxReqCode > reqCode) {
            Log.d(TAG, "setAlarms: Cancelling an alarm of reqCode = $reqCode")
            alarmIntent = PendingIntent.getBroadcast(
                ctx,
                reqCode++,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(alarmIntent)
        }
        sharedPreferences.edit().putBoolean("pref_alarms_set", true).apply()
    }

    private fun groupByStartDates(items: List<FoodItem>): Map<MonthDay, List<FoodItem>> {
        val itemsByStartDay1 = items.filter { it.startDay1 != null }.groupBy { it.startDay1!! }
        val itemsByStartDay2 = items.filter { it.startDay2 != null }.groupBy { it.startDay2!! }

        return itemsByStartDay1 + itemsByStartDay2
    }

    private fun groupByEndDates(items: List<FoodItem>): Map<MonthDay, List<FoodItem>> {
        val itemsByEndDay1 = items.filter { it.endDay1 != null }.groupBy { it.endDay1!! }
        val itemsByEndDay2 = items.filter { it.endDay2 != null }.groupBy { it.endDay2!! }

        return itemsByEndDay1 + itemsByEndDay2
    }
}
