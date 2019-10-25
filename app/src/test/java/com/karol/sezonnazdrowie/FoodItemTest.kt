package com.karol.sezonnazdrowie

import com.karol.sezonnazdrowie.data.FoodItem
import com.prolificinteractive.materialcalendarview.CalendarDay

import junit.framework.TestCase

class FoodItemTest : TestCase() {

    fun testGetNearestSeasonDay() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)),
            CalendarDay.from(2016, 2, 14)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 9, 30)),
            CalendarDay.from(2016, 9, 30)
        )

        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = CalendarDay.from(1970, 1, 15),
            endDay1 = CalendarDay.from(1970, 3, 30)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 1, 14)),
            CalendarDay.from(2016, 1, 15)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)),
            CalendarDay.from(2016, 2, 14)
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 1, 15),
            endDay1 = CalendarDay.from(1970, 3, 30),
            startDay2 = CalendarDay.from(1970, 9, 1),
            endDay2 = CalendarDay.from(1970, 11, 31)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 1, 14)),
            CalendarDay.from(2016, 1, 15)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)),
            CalendarDay.from(2016, 2, 14)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 5, 14)),
            CalendarDay.from(2016, 9, 1)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 9, 1)),
            CalendarDay.from(2016, 9, 1)
        )
        assertEquals(
            item.getNearestSeasonDay(CalendarDay.from(2016, 9, 19)),
            CalendarDay.from(2016, 9, 19)
        )

        // TODO Use kluent
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 11, 15),
            endDay1 = CalendarDay.from(1970, 3, 30)
        )
        assertEquals(
            "Equals",
            CalendarDay.from(2016, 11, 15),
            item.getNearestSeasonDay(CalendarDay.from(2016, 6, 14))
        )
        assertEquals(
            "Equals",
            CalendarDay.from(2016, 2, 10),
            item.getNearestSeasonDay(CalendarDay.from(2016, 2, 10))
        )
    }

    fun testGetNearestSeasonStart() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertNull(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)))
        assertNull(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)))

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = CalendarDay.from(1970, 1, 15),
            endDay1 = CalendarDay.from(1970, 3, 30)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)),
            CalendarDay.from(2016, 1, 15)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)),
            CalendarDay.from(2017, 1, 15)
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 1, 15),
            endDay1 = CalendarDay.from(1970, 3, 30),
            startDay2 = CalendarDay.from(1970, 9, 1),
            endDay2 = CalendarDay.from(1970, 11, 31)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)),
            CalendarDay.from(2016, 1, 15)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)),
            CalendarDay.from(2016, 9, 1)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 8, 13)),
            CalendarDay.from(2016, 9, 1)
        )

        // check days equal as compare days
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 9, 1)),
            CalendarDay.from(2016, 9, 1)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 1, 15)),
            CalendarDay.from(2016, 1, 15)
        )

        //check when date is after second season start
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 9, 15)),
            CalendarDay.from(2017, 1, 15)
        )

        // check when end date is before start date
        item = FoodItem(
            true,
            "BRUKSELKA",
            startDay1 = CalendarDay.from(1970, 9, 1),
            endDay1 = CalendarDay.from(1970, 2, 15)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 9, 1)),
            CalendarDay.from(2016, 9, 1)
        )
        assertEquals(
            item.getNearestSeasonStart(CalendarDay.from(2016, 9, 10)),
            CalendarDay.from(2017, 9, 1)
        )
    }

    fun testGetNearestSeasonEnd() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertNull(item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)))
        assertNull(item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)))

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = CalendarDay.from(1970, 1, 15),
            endDay1 = CalendarDay.from(1970, 3, 30)
        )
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)),
            CalendarDay.from(2016, 3, 30)
        )
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)),
            CalendarDay.from(2017, 3, 30)
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 1, 15),
            endDay1 = CalendarDay.from(1970, 3, 30),
            startDay2 = CalendarDay.from(1970, 9, 1),
            endDay2 = CalendarDay.from(1970, 11, 10)
        )
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)),
            CalendarDay.from(2016, 3, 30)
        )
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)),
            CalendarDay.from(2016, 11, 10)
        )
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 8, 13)),
            CalendarDay.from(2016, 11, 10)
        )
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 11, 13)),
            CalendarDay.from(2017, 3, 30)
        )

        // check days equal as compare days
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 11, 10)),
            CalendarDay.from(2016, 11, 10)
        )
        assertEquals(
            item.getNearestSeasonEnd(CalendarDay.from(2016, 3, 30)),
            CalendarDay.from(2016, 3, 30)
        )
    }
}