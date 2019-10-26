package com.karol.sezonnazdrowie

import com.karol.sezonnazdrowie.data.FoodItem
import com.prolificinteractive.materialcalendarview.CalendarDay
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue

import org.junit.Test

class FoodItemTest {

    @Test
    fun testGetNearestSeasonDay() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertEquals(
            CalendarDay.from(2016, 3, 14),
            item.getNearestSeasonDay(CalendarDay.from(2016, 3, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 10, 30),
            item.getNearestSeasonDay(CalendarDay.from(2016, 10, 30))
        )

        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = CalendarDay.from(1970, 2, 15),
            endDay1 = CalendarDay.from(1970, 4, 30)
        )
        assertEquals(
            CalendarDay.from(2016, 2, 15),
            item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 3, 14),
            item.getNearestSeasonDay(CalendarDay.from(2016, 3, 14))
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 2, 15),
            endDay1 = CalendarDay.from(1970, 4, 30),
            startDay2 = CalendarDay.from(1970, 10, 1),
            endDay2 = CalendarDay.from(1970, 1, 10)
        )
        assertEquals(
            CalendarDay.from(2016, 2, 15),
            item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 3, 14),
            item.getNearestSeasonDay(CalendarDay.from(2016, 3, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 10, 1),
            item.getNearestSeasonDay(CalendarDay.from(2016, 6, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 10, 1),
            item.getNearestSeasonDay(CalendarDay.from(2016, 10, 1))
        )
        assertEquals(
            CalendarDay.from(2016, 10, 19),
            item.getNearestSeasonDay(CalendarDay.from(2016, 10, 19))
        )

        assertEquals(
            CalendarDay.from(2016, 10, 1),
            item.getNearestSeasonDay(CalendarDay.from(2016, 9, 1))
        )

        // TODO Use kluent
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 12, 15),
            endDay1 = CalendarDay.from(1970, 4, 30)
        )
        assertEquals(
            CalendarDay.from(2016, 12, 15),
            item.getNearestSeasonDay(CalendarDay.from(2016, 7, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 3, 10),
            item.getNearestSeasonDay(CalendarDay.from(2016, 3, 10))
        )
    }

    @Test
    fun testGetNearestSeasonStart() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertNull(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)))
        assertNull(item.getNearestSeasonStart(CalendarDay.from(2016, 3, 14)))

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = CalendarDay.from(1970, 2, 15),
            endDay1 = CalendarDay.from(1970, 4, 30)
        )
        assertEquals(
            CalendarDay.from(2016, 2, 15),
            item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14))
        )
        assertEquals(
            CalendarDay.from(2017, 2, 15),
            item.getNearestSeasonStart(CalendarDay.from(2016, 3, 14))
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 2, 15),
            endDay1 = CalendarDay.from(1970, 4, 30),
            startDay2 = CalendarDay.from(1970, 10, 1),
            endDay2 = CalendarDay.from(1970, 12, 31)
        )
        assertEquals(
            CalendarDay.from(2016, 2, 15),
            item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 10, 1),
            item.getNearestSeasonStart(CalendarDay.from(2016, 3, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 10, 1),
            item.getNearestSeasonStart(CalendarDay.from(2016, 9, 13))
        )

        // check days equal as compare days
        assertEquals(
            CalendarDay.from(2016, 10, 1),
            item.getNearestSeasonStart(CalendarDay.from(2016, 10, 1))
        )
        assertEquals(
            CalendarDay.from(2016, 2, 15),
            item.getNearestSeasonStart(CalendarDay.from(2016, 2, 15))
        )

        //check when date is after second season start
        assertEquals(
            CalendarDay.from(2017, 2, 15),
            item.getNearestSeasonStart(CalendarDay.from(2016, 10, 15))
        )

        // check when end date is before start date
        item = FoodItem(
            true,
            "BRUKSELKA",
            startDay1 = CalendarDay.from(1970, 10, 1),
            endDay1 = CalendarDay.from(1970, 3, 15)
        )
        assertEquals(
            CalendarDay.from(2016, 10, 1),
            item.getNearestSeasonStart(CalendarDay.from(2016, 10, 1))
        )
        assertEquals(
            CalendarDay.from(2017, 10, 1),
            item.getNearestSeasonStart(CalendarDay.from(2016, 10, 10))
        )
    }

    @Test
    fun testGetNearestSeasonEnd() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertNull(item.getNearestSeasonEnd(CalendarDay.from(2016, 2, 14)))
        assertNull(item.getNearestSeasonEnd(CalendarDay.from(2016, 5, 14)))

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = CalendarDay.from(1970, 2, 15),
            endDay1 = CalendarDay.from(1970, 4, 30)
        )
        assertEquals(
            CalendarDay.from(2016, 4, 30),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 2, 14))
        )
        assertEquals(
            CalendarDay.from(2017, 4, 30),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 5, 14))
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = CalendarDay.from(1970, 2, 15),
            endDay1 = CalendarDay.from(1970, 4, 30),
            startDay2 = CalendarDay.from(1970, 10, 1),
            endDay2 = CalendarDay.from(1970, 12, 10)
        )
        assertEquals(
            CalendarDay.from(2016, 4, 30),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 2, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 12, 10),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 5, 14))
        )
        assertEquals(
            CalendarDay.from(2016, 12, 10),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 9, 13))
        )
        assertEquals(
            CalendarDay.from(2017, 4, 30),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 12, 13))
        )

        // check days equal as compare days
        assertEquals(
            CalendarDay.from(2016, 12, 10),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 12, 10))
        )
        assertEquals(
            CalendarDay.from(2016, 4, 30),
            item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 30))
        )
    }

    @Test
    fun `given has any of proximates should return true when asked if has proximates`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", water = "2mg")

        // when
        val hasProximates = foodItem.hasProximates()

        // then
        assertTrue(hasProximates)
    }

    @Test
    fun `given has no proximates should return false when asked if has proximates`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER")

        // when
        val hasProximates = foodItem.hasProximates()

        // then
        assertFalse(hasProximates)
    }

    @Test
    fun `given has any of minerals should return true when asked if has minerals`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", calcium = "2mg")

        // when
        val hasMinerals = foodItem.hasMinerals()

        // then
        assertTrue(hasMinerals)
    }

    @Test
    fun `given has no minerals should return false when asked if has minerals`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER")

        // when
        val hasMinerals = foodItem.hasMinerals()

        // then
        assertFalse(hasMinerals)
    }

    @Test
    fun `given has any of vitamins should return true when asked if has vitamins`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", vitA = "2mg")

        // when
        val hasVitamins = foodItem.hasVitamins()

        // then
        assertTrue(hasVitamins)
    }

    @Test
    fun `given has no vitamins should return false when asked if has vitamins`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER")

        // when
        val hasVitamins = foodItem.hasVitamins()

        // then
        assertFalse(hasVitamins)
    }
}