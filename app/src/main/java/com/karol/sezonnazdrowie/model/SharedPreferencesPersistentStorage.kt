package com.karol.sezonnazdrowie.model

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.karol.sezonnazdrowie.data.FoodItem
import org.threeten.bp.LocalTime

class SharedPreferencesPersistentStorage(context: Context) : PersistentStorage {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override var seasonStartPeriods: Set<String>
        get() = sharedPreferences.getStringSet("pref_season_start", null)
            ?: setOf(SnzAlarmManager.PreNotiTimePeriod.WeekBefore.prefValue)
        set(value) {
            sharedPreferences.edit()
                .putStringSet("pref_season_start", value)
                .apply()
        }
    override var seasonEndPeriods: Set<String>
        get() = sharedPreferences.getStringSet("pref_season_end", null)
            ?: setOf(SnzAlarmManager.PreNotiTimePeriod.WeekBefore.prefValue)
        set(value) {
            sharedPreferences.edit()
                .putStringSet("pref_season_end", value)
                .apply()
        }
    override var notificationTime: LocalTime
        get() = LocalTime.ofNanoOfDay(
            sharedPreferences.getLong("pref_notification_time", 72000000000000L)
        )
        set(value) {
            sharedPreferences.edit()
                .putLong("pref_notification_time", value.toNanoOfDay())
                .apply()
        }
    override var lastSetAlarmId: Int
        get() = sharedPreferences.getInt("maxReqCode", 0)
        set(value) {
            sharedPreferences.edit()
                .putInt("maxReqCode", value)
                .apply()
        }

    override var lastAlarmSetTime: Long
        get() = sharedPreferences.getLong("pref_alarms_set", 0L)
        set(value) {
            sharedPreferences.edit()
                .putLong("pref_alarms_set", value)
                .apply()
        }

    override fun isFoodItemNotificationEnabled(foodItem: FoodItem): Boolean =
        sharedPreferences.getBoolean("pref_noti_${foodItem.name}", true)
}