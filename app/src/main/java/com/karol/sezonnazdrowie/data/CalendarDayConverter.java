package com.karol.sezonnazdrowie.data;

import androidx.room.TypeConverter;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;

public class CalendarDayConverter {
    @TypeConverter
    public static CalendarDay fromTimestamp(Long value) {
        Calendar cal = Calendar.getInstance();
        if (value == null) {
            return null;
        }
        cal.setTimeInMillis(value);
        return CalendarDay.from(cal);
    }

    @TypeConverter
    public static Long dateToTimestamp(CalendarDay date) {
        return date == null ? null : date.getCalendar().getTimeInMillis();
    }
}
