package com.karol.sezonnazdrowie.model

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.karol.sezonnazdrowie.data.FoodItem
import org.threeten.bp.LocalTime

const val PREF_SEASON_START = "pref_season_start"
const val PREF_SEASON_END = "pref_season_end"
const val PREF_NOTIFICATION_TIME = "pref_notification_time"
const val PREF_ALARMS_SET = "pref_alarms_set"
const val PREF_MAX_REQ_CODE = "maxReqCode"
const val PREF_FOOD_ITEM_PREFIX = "pref_noti_"
const val PREF_NOTIFICATION_FRUIT = "pref_notification_fruit"
const val PREF_NOTIFICATION_VEGETABLE = "pref_notification_vegetable"

class SharedPreferencesPersistentStorage(context: Context) : PersistentStorage {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override var seasonStartPeriods: Set<String>
        get() = sharedPreferences.getStringSet(PREF_SEASON_START, null)
            ?: setOf(SnzAlarmManager.PreNotiTimePeriod.WeekBefore.prefValue)
        set(value) {
            sharedPreferences.edit()
                .putStringSet(PREF_SEASON_START, value)
                .apply()
        }
    override var seasonEndPeriods: Set<String>
        get() = sharedPreferences.getStringSet(PREF_SEASON_END, null)
            ?: setOf(SnzAlarmManager.PreNotiTimePeriod.WeekBefore.prefValue)
        set(value) {
            sharedPreferences.edit()
                .putStringSet(PREF_SEASON_END, value)
                .apply()
        }
    override var notificationTime: LocalTime
        get() = LocalTime.ofNanoOfDay(
            sharedPreferences.getLong(PREF_NOTIFICATION_TIME, 72000000000000L)
        )
        set(value) {
            sharedPreferences.edit()
                .putLong(PREF_NOTIFICATION_TIME, value.toNanoOfDay())
                .apply()
        }
    override var lastSetAlarmId: Int
        get() = sharedPreferences.getInt(PREF_MAX_REQ_CODE, 0)
        set(value) {
            sharedPreferences.edit()
                .putInt(PREF_MAX_REQ_CODE, value)
                .apply()
        }

    override var lastAlarmSetTime: Long
        get() = sharedPreferences.getLong(PREF_ALARMS_SET, 0L)
        set(value) {
            sharedPreferences.edit()
                .putLong(PREF_ALARMS_SET, value)
                .apply()
        }

    override fun isFoodItemNotificationEnabled(foodItem: FoodItem): Boolean =
        sharedPreferences.getBoolean("${PREF_FOOD_ITEM_PREFIX}${foodItem.name}", true)
}