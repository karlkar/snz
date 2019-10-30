package com.karol.sezonnazdrowie.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import com.karol.sezonnazdrowie.R
import com.karol.sezonnazdrowie.data.Database
import com.karol.sezonnazdrowie.data.FoodItem
import com.karol.sezonnazdrowie.data.SnzDatabase
import com.karol.sezonnazdrowie.view.controls.TimePreference
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
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

    private interface PrecedingPeriodObtainer {
        fun getPrecedingPeriod(): List<PreNotiTimePeriod>
    }

    private class StartPrecedingDaysObtainer(
        private val sharedPreferences: SharedPreferences
    ) : PrecedingPeriodObtainer {

        override fun getPrecedingPeriod(): List<PreNotiTimePeriod> {
            val seasonStart = sharedPreferences.getStringSet(
                "pref_season_start",
                null
            ) ?: setOf(PreNotiTimePeriod.MonthBefore.prefValue)
            return seasonStart.map { PreNotiTimePeriod.fromPrefValue(it) }
        }
    }

    private class EndPrecedingDaysObtainer(
        private val sharedPreferences: SharedPreferences
    ) : PrecedingPeriodObtainer {

        override fun getPrecedingPeriod(): List<PreNotiTimePeriod> {
            val seasonStart = sharedPreferences.getStringSet(
                "pref_season_end",
                null
            ) ?: setOf(PreNotiTimePeriod.MonthBefore.prefValue)
            return seasonStart.map { PreNotiTimePeriod.fromPrefValue(it) }
        }
    }

    private interface PrecedingPeriodMapper {
        fun mapToStringResource(daysAmount: PreNotiTimePeriod): Int
    }

    private class StartPrecedingDaysMapper : PrecedingPeriodMapper {

        override fun mapToStringResource(daysAmount: PreNotiTimePeriod): Int {
            return when (daysAmount) {
                PreNotiTimePeriod.AtDay -> R.string.season_starts_soon
                PreNotiTimePeriod.WeekBefore -> R.string.season_starts_week
                PreNotiTimePeriod.MonthBefore -> R.string.season_starts_month
            }
        }
    }

    private class EndPrecedingDaysMapper : PrecedingPeriodMapper {

        override fun mapToStringResource(daysAmount: PreNotiTimePeriod): Int {
            return when (daysAmount) {
                PreNotiTimePeriod.AtDay -> R.string.season_ends_soon
                PreNotiTimePeriod.WeekBefore -> R.string.season_ends_week
                PreNotiTimePeriod.MonthBefore -> throw IllegalArgumentException("Month is not supported for end dates")
            }
        }
    }

    /**
     * @return amount of alarms created
     */
    private fun createAlarmsFor(
        timeDataProvider: TimeDataProvider,
        map: Map<MonthDay, List<FoodItem>>,
        precedingPeriodObtainer: PrecedingPeriodObtainer,
        precedingDaysMapper: PrecedingPeriodMapper,
        isStart: Boolean,
        initialAlarmId: Int,
        ctx: Context,
        sharedPreferences: SharedPreferences,
        alarmManager: AlarmManager
    ): Int {
        val today = timeDataProvider.getCurrentDay()
        val zoneOffset = timeDataProvider.getZoneOffset()

        var alarmId = initialAlarmId

        val notiTime = sharedPreferences.getString("pref_notification_hour", null) ?: "20:00"
        val notiHour = TimePreference.getHour(notiTime)
        val notiMinute = TimePreference.getMinute(notiTime)

        val precedingPeriodList = precedingPeriodObtainer.getPrecedingPeriod()

        map.entries.forEach {
            val notificationText = it.value
                .filter { foodItem ->
                    sharedPreferences.getBoolean("pref_noti_${foodItem.name}", true)
                }.joinToString { foodItem -> foodItem.conjugatedName }

            if (notificationText.isNotEmpty()) {
                daysIter@ for (precedingPeriod in precedingPeriodList) {
                    val titleRes: Int
                    try {
                        titleRes = precedingDaysMapper.mapToStringResource(precedingPeriod)
                    } catch (ex: IllegalArgumentException) {
                        continue@daysIter
                    }
                    val alarmIntent = createBroadcastNotificationIntent(
                        ctx,
                        isStart,
                        alarmId++,
                        titleRes,
                        notificationText
                    )

                    val notificationDateTime = createNotificationDateTime(
                        precedingPeriod,
                        it.key,
                        today,
                        notiHour,
                        notiMinute
                    )

                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        notificationDateTime.toInstant(zoneOffset).toEpochMilli(),
                        alarmIntent
                    )
                    Log.d(TAG, "setAlarms: Alarm set @ $notificationDateTime")
                }
            }
        }
        return alarmId
    }

    private fun createBroadcastNotificationIntent(
        ctx: Context,
        isStart: Boolean,
        alarmId: Int,
        @StringRes titleRes: Int,
        notificationText: String
    ): PendingIntent? {
        val intent = Intent(ctx, Receiver::class.java).apply {
            putExtra("type", if (isStart) "start" else "end")
            putExtra("reqCode", alarmId)
            putExtra("titleRes", titleRes)
            putExtra("text", notificationText)
        }
        return PendingIntent.getBroadcast(
            ctx,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotificationDateTime(
        preNotiTimePeriod: PreNotiTimePeriod,
        monthDay: MonthDay,
        today: LocalDate,
        notiHour: Int,
        notiMinute: Int
    ): LocalDateTime {
        return when (preNotiTimePeriod) {
            PreNotiTimePeriod.AtDay ->
                monthDay.atYear(today.year)
            PreNotiTimePeriod.WeekBefore ->
                monthDay.atYear(today.year).minusWeeks(1)
            PreNotiTimePeriod.MonthBefore ->
                monthDay.atYear(today.year).minusMonths(1)
        }.let { obtainedDate ->
            if (obtainedDate.isBefore(today)) {
                obtainedDate.plusYears(1)
            } else {
                obtainedDate
            }
        }.atTime(notiHour, notiMinute)
    }

    internal fun setAlarms(
        ctx: Context,
        currentDayProvider: TimeDataProvider,
        sharedPreferences: SharedPreferences,
        alarmManager: AlarmManager,
        database: Database
    ) {
        val lastCreatedAlarmId = createAlarms(
            ctx,
            sharedPreferences,
            alarmManager,
            database,
            currentDayProvider
        )

        clearPreviousAlarms(
            ctx,
            sharedPreferences,
            alarmManager,
            lastCreatedAlarmId
        )

        sharedPreferences.edit()
            .putInt("maxReqCode", lastCreatedAlarmId)
            .putBoolean("pref_alarms_set", true)
            .apply()
    }

    private fun createAlarms(
        ctx: Context,
        sharedPreferences: SharedPreferences,
        alarmManager: AlarmManager,
        database: Database,
        currentDayProvider: TimeDataProvider
    ): Int {
        val allItems = database.allFruits + database.allVegetables
        val startMap = groupByStartDates(allItems)
        val endMap = groupByEndDates(allItems)
        var reqCode = createAlarmsFor(
            currentDayProvider,
            startMap,
            StartPrecedingDaysObtainer(sharedPreferences),
            StartPrecedingDaysMapper(),
            true,
            1,
            ctx,
            sharedPreferences,
            alarmManager
        )

        reqCode = createAlarmsFor(
            currentDayProvider,
            endMap,
            EndPrecedingDaysObtainer(sharedPreferences),
            EndPrecedingDaysMapper(),
            false,
            reqCode,
            ctx,
            sharedPreferences,
            alarmManager
        )
        return reqCode - 1
    }

    private fun clearPreviousAlarms(
        ctx: Context,
        sharedPreferences: SharedPreferences,
        alarmManager: AlarmManager,
        lastCreatedAlarmId: Int
    ) {
        val prevMaxReqCode = sharedPreferences.getInt("maxReqCode", 0)
        Log.d(TAG, "setAlarms: Ending with $lastCreatedAlarmId, previous ending $prevMaxReqCode")

        val intent = Intent(ctx, Receiver::class.java)
        for (id in (lastCreatedAlarmId + 1)..prevMaxReqCode) {
            Log.d(TAG, "setAlarms: Cancelling an alarm of id = $id")
            val alarmIntent = PendingIntent.getBroadcast(
                ctx,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(alarmIntent)
        }
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
