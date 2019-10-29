package com.karol.sezonnazdrowie.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
// TODO Write tests
    private const val TAG = "SNZALARMMANAGER"

    fun startSetAlarmsTask(ctx: Context, database: SnzDatabase) {
        Executors.newSingleThreadExecutor().submit {
            setAlarms(ctx, database)
        }
    }

    private interface PrecedingDaysObtainer {
        fun getPrecedingDays(): List<Int>
    }

    private class StartPrecedingDaysObtainer(
        private val ctx: Context,
        private val sharedPreferences: SharedPreferences
    ) : PrecedingDaysObtainer {

        override fun getPrecedingDays(): List<Int> {
            val notificationShowPreceding = mutableListOf<Int>()
            val seasonStart = sharedPreferences.getStringSet("pref_season_start", null)
            if (seasonStart == null) {
                notificationShowPreceding.add(7)
            } else {
                if (seasonStart.contains(ctx.getString(R.string.at_the_start_day))) {
                    notificationShowPreceding.add(0)
                }
                if (seasonStart.contains(ctx.getString(R.string.week_before))) {
                    notificationShowPreceding.add(7)
                }
                if (seasonStart.contains(ctx.getString(R.string.month_before))) {
                    notificationShowPreceding.add(30)
                }
            }
            return notificationShowPreceding
        }
    }

    private class EndPrecedingDaysObtainer(
        private val ctx: Context,
        private val sharedPreferences: SharedPreferences
    ) : PrecedingDaysObtainer {

        override fun getPrecedingDays(): List<Int> {
            val notificationEndShowPreceding = mutableListOf<Int>()
            val seasonEnd = sharedPreferences.getStringSet("pref_season_end", null)
            if (seasonEnd == null) {
                notificationEndShowPreceding.add(7)
            } else {
                if (seasonEnd.contains(ctx.getString(R.string.at_the_end_day))) {
                    notificationEndShowPreceding.add(0)
                }
                if (seasonEnd.contains(ctx.getString(R.string.week_before))) {
                    notificationEndShowPreceding.add(7)
                }
            }
            return notificationEndShowPreceding
        }
    }

    private interface PrecedingDaysMapper {
        fun mapToString(daysAmount: Int): String
    }

    private class StartPrecedingDaysMapper(private val ctx: Context) : PrecedingDaysMapper {

        override fun mapToString(daysAmount: Int): String {
            return when (daysAmount) {
                0 -> ctx.getString(R.string.season_starts_soon)
                7 -> ctx.getString(R.string.season_starts_week)
                30 -> ctx.getString(R.string.season_starts_month)
                else -> throw IllegalArgumentException("Unsupported amount of $daysAmount days.")
            }
        }
    }

    private class EndPrecedingDaysMapper(private val ctx: Context) : PrecedingDaysMapper {

        override fun mapToString(daysAmount: Int): String {
            return when (daysAmount) {
                0 -> ctx.getString(R.string.season_ends_soon)
                7 -> ctx.getString(R.string.season_ends_week)
                else -> throw IllegalArgumentException("Unsupported amount of $daysAmount days.")
            }
        }
    }

    private fun createAlarmsFor(
        map: Map<MonthDay, List<FoodItem>>,
        precedingDaysObtainer: PrecedingDaysObtainer,
        precedingDaysMapper: PrecedingDaysMapper,
        isStart: Boolean,
        ctx: Context,
        sharedPreferences: SharedPreferences,
        alarmManager: AlarmManager
    ): Int {
        val today = LocalDate.now()
        val zoneOffset = OffsetDateTime.now().offset

        var reqCode = 1

        val notiTime = sharedPreferences.getString("pref_notification_hour", "20:00")
        val notiHour = TimePreference.getHour(notiTime!!)
        val notiMinute = TimePreference.getMinute(notiTime)

        val precedingDays = precedingDaysObtainer.getPrecedingDays()

        map.entries.forEach {
            val notificationText = it.value
                .filter { foodItem ->
                    sharedPreferences.getBoolean("pref_noti_${foodItem.name}", true)
                }
                .map { foodItem -> foodItem.conjugatedName }
                .joinToString()

            if (notificationText.isEmpty()) {
                daysIter@ for (dayDiff in precedingDays) {
                    val title: String
                    try {
                        title = precedingDaysMapper.mapToString(dayDiff)
                    } catch (ex: IllegalArgumentException) {
                        continue@daysIter
                    }
                    val intent = Intent(ctx, Receiver::class.java).apply {
                        putExtra("type", if (isStart) "start" else "end")
                        putExtra("reqCode", reqCode)
                        putExtra("title", title)
                        putExtra("text", notificationText)
                    }
                    val alarmIntent = PendingIntent.getBroadcast(
                        ctx,
                        reqCode++,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val notificationDate = it.key.atYear(today.year)
                    with(notificationDate) {
                        minusDays(dayDiff.toLong())
                        if (isBefore(today)) {
                            plusYears(1)
                        }
                    }
                    val notificationDateTime = notificationDate.atTime(notiHour, notiMinute)

                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        notificationDateTime.toInstant(zoneOffset).toEpochMilli(),
                        alarmIntent
                    )
                    Log.d(TAG, "setAlarms: Alarm set @ $notificationDateTime")
                }
            }
        }
        return reqCode
    }

    internal fun setAlarms(ctx: Context, database: SnzDatabase) {
        val allItems = database.allFruits + database.allVegetables
        val startMap = groupByStartDates(allItems)
        val endMap = groupByEndDates(allItems)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var reqCode = createAlarmsFor(
            startMap,
            StartPrecedingDaysObtainer(ctx, sharedPreferences),
            StartPrecedingDaysMapper(ctx),
            true,
            ctx,
            sharedPreferences,
            alarmManager
        )

        reqCode += createAlarmsFor(
            endMap,
            EndPrecedingDaysObtainer(ctx, sharedPreferences),
            EndPrecedingDaysMapper(ctx),
            false,
            ctx,
            sharedPreferences,
            alarmManager
        )

        val prevMaxReqCode = sharedPreferences.getInt("maxReqCode", 0)
        Log.d(TAG, "setAlarms: Ending with $reqCode, previous ending $prevMaxReqCode")
        sharedPreferences.edit().putInt("maxReqCode", reqCode).apply()

        val intent = Intent(ctx, Receiver::class.java)
        while (prevMaxReqCode > reqCode) {
            Log.d(TAG, "setAlarms: Cancelling an alarm of reqCode = $reqCode")
            val alarmIntent = PendingIntent.getBroadcast(
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
