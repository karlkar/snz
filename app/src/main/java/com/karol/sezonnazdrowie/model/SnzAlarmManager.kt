package com.karol.sezonnazdrowie.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.Database
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.data.SnzDatabase
import com.karol.sezonnazdrowie.view.controls.TimePreference
import org.threeten.bp.LocalDate
import org.threeten.bp.MonthDay
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.util.concurrent.Executors

object SnzAlarmManager {
    private const val TAG = "SNZALARMMANAGER"

    fun startSetAlarmsTask(ctx: Context, database: SnzDatabase) {
        val currentDayProvider = TimeDataProviderImpl()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Executors.newSingleThreadExecutor().submit {
            setAlarms(ctx, currentDayProvider, sharedPreferences, alarmManager, database)
        }
    }

    enum class PreNotiTimePeriod(val prefValue: String) {
        AtDay("DAY"),
        WeekBefore("WEEK"),
        MonthBefore("MONTH");

        companion object {
            fun fromPrefValue(requestedValue: String) =
                values().firstOrNull { it.prefValue == requestedValue }
                    ?: throw IllegalArgumentException("Unknown period of '$requestedValue'")
        }
    }

    interface TimeDataProvider {
        fun getCurrentDay(): LocalDate
        fun getZoneOffset(): ZoneOffset
    }

    class TimeDataProviderImpl : TimeDataProvider {
        override fun getCurrentDay(): LocalDate = LocalDate.now()
        override fun getZoneOffset(): ZoneOffset = OffsetDateTime.now().offset
    }

    private interface PrecedingDaysObtainer {
        fun getPrecedingDays(): List<PreNotiTimePeriod>
    }

    private class StartPrecedingDaysObtainer(
        private val sharedPreferences: SharedPreferences
    ) : PrecedingDaysObtainer {

        override fun getPrecedingDays(): List<PreNotiTimePeriod> {
            val seasonStart = sharedPreferences.getStringSet(
                "pref_season_start",
                null
            ) ?: setOf(PreNotiTimePeriod.MonthBefore.prefValue)
            return seasonStart.map { PreNotiTimePeriod.fromPrefValue(it) }
        }
    }

    private class EndPrecedingDaysObtainer(
        private val sharedPreferences: SharedPreferences
    ) : PrecedingDaysObtainer {

        override fun getPrecedingDays(): List<PreNotiTimePeriod> {
            val seasonStart = sharedPreferences.getStringSet(
                "pref_season_end",
                null
            ) ?: setOf(PreNotiTimePeriod.MonthBefore.prefValue)
            return seasonStart.map { PreNotiTimePeriod.fromPrefValue(it) }
        }
    }

    private interface PrecedingDaysMapper {
        fun mapToString(daysAmount: PreNotiTimePeriod): String
    }

    private class StartPrecedingDaysMapper(private val ctx: Context) : PrecedingDaysMapper {

        override fun mapToString(daysAmount: PreNotiTimePeriod): String {
            return when (daysAmount) {
                PreNotiTimePeriod.AtDay -> ctx.getString(R.string.season_starts_soon)
                PreNotiTimePeriod.WeekBefore -> ctx.getString(R.string.season_starts_week)
                PreNotiTimePeriod.MonthBefore -> ctx.getString(R.string.season_starts_month)
            }
        }
    }

    private class EndPrecedingDaysMapper(private val ctx: Context) : PrecedingDaysMapper {

        override fun mapToString(daysAmount: PreNotiTimePeriod): String {
            return when (daysAmount) {
                PreNotiTimePeriod.AtDay -> ctx.getString(R.string.season_ends_soon)
                PreNotiTimePeriod.WeekBefore -> ctx.getString(R.string.season_ends_week)
                PreNotiTimePeriod.MonthBefore -> throw IllegalArgumentException("Month is not supported for end dates")
            }
        }
    }

    private fun createAlarmsFor(
        timeDataProvider: TimeDataProvider,
        map: Map<MonthDay, List<FoodItem>>,
        precedingDaysObtainer: PrecedingDaysObtainer,
        precedingDaysMapper: PrecedingDaysMapper,
        isStart: Boolean,
        ctx: Context,
        sharedPreferences: SharedPreferences,
        alarmManager: AlarmManager
    ): Int {
        val today = timeDataProvider.getCurrentDay()
        val zoneOffset = timeDataProvider.getZoneOffset()

        var reqCode = 1

        val notiTime = sharedPreferences.getString("pref_notification_hour", "20:00")
        val notiHour = TimePreference.getHour(notiTime!!)
        val notiMinute = TimePreference.getMinute(notiTime)

        val precedingDays = precedingDaysObtainer.getPrecedingDays()

        map.entries.forEach {
            val notificationText = it.value
                .filter { foodItem ->
                    sharedPreferences.getBoolean("pref_noti_${foodItem.name}", true)
                }.joinToString { foodItem -> foodItem.conjugatedName }

            if (notificationText.isNotEmpty()) {
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

                    val notificationDate = when (dayDiff) {
                        PreNotiTimePeriod.AtDay -> it.key.atYear(today.year)
                        PreNotiTimePeriod.WeekBefore ->
                            it.key.atYear(today.year).minusWeeks(1)
                        PreNotiTimePeriod.MonthBefore ->
                            it.key.atYear(today.year).minusMonths(1)
                    }.let { obtainedDate ->
                        if (obtainedDate.isBefore(today)) {
                            obtainedDate.plusYears(1)
                        } else {
                            obtainedDate
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

    internal fun setAlarms(
        ctx: Context,
        currentDayProvider: TimeDataProvider,
        sharedPreferences: SharedPreferences,
        alarmManager: AlarmManager,
        database: Database
    ) {
        val allItems = database.allFruits + database.allVegetables
        val startMap = groupByStartDates(allItems)
        val endMap = groupByEndDates(allItems)

        var reqCode = createAlarmsFor(
            currentDayProvider,
            startMap,
            StartPrecedingDaysObtainer(sharedPreferences),
            StartPrecedingDaysMapper(ctx),
            true,
            ctx,
            sharedPreferences,
            alarmManager
        )

        reqCode += createAlarmsFor(
            currentDayProvider,
            endMap,
            EndPrecedingDaysObtainer(sharedPreferences),
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
