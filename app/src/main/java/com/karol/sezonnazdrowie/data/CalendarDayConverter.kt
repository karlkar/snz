package com.karol.sezonnazdrowie.data

import androidx.room.TypeConverter

import com.prolificinteractive.materialcalendarview.CalendarDay

import java.util.Calendar

class CalendarDayConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): CalendarDay? {
        val cal = Calendar.getInstance()
        return if (value == null) {
            null
        } else {
            cal.timeInMillis = value
            CalendarDay.from(cal)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: CalendarDay?): Long? = date?.calendar?.timeInMillis
}
