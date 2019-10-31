package com.karol.sezonnazdrowie.model

import com.karol.sezonnazdrowie.data.FoodItem
import org.threeten.bp.LocalTime

interface PersistentStorage {
    var seasonStartPeriods: Set<String>
    var seasonEndPeriods: Set<String>
    var notificationTime: LocalTime
    var lastSetAlarmId: Int
    var lastAlarmSetTime: Long

    fun isFoodItemNotificationEnabled(foodItem: FoodItem): Boolean
}