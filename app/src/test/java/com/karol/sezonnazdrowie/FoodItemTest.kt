package com.karol.sezonnazdrowie

import com.karol.sezonnazdrowie.data.FoodItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.MonthDay

class FoodItemTest {

    @Test
    fun testGetNearestSeasonDay() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertEquals(
            LocalDate.of(2016, 3, 14),
            item.getNearestSeasonDay(LocalDate.of(2016, 3, 14))
        )
        assertEquals(
            LocalDate.of(2016, 10, 30),
            item.getNearestSeasonDay(LocalDate.of(2016, 10, 30))
        )

        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        assertEquals(
            LocalDate.of(2016, 2, 15),
            item.getNearestSeasonDay(LocalDate.of(2016, 2, 14))
        )
        assertEquals(
            LocalDate.of(2016, 3, 14),
            item.getNearestSeasonDay(LocalDate.of(2016, 3, 14))
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30),
            startDay2 = MonthDay.of(10, 1),
            endDay2 = MonthDay.of(1, 10)
        )
        assertEquals(
            LocalDate.of(2016, 2, 15),
            item.getNearestSeasonDay(LocalDate.of(2016, 2, 14))
        )
        assertEquals(
            LocalDate.of(2016, 3, 14),
            item.getNearestSeasonDay(LocalDate.of(2016, 3, 14))
        )
        assertEquals(
            LocalDate.of(2016, 10, 1),
            item.getNearestSeasonDay(LocalDate.of(2016, 6, 14))
        )
        assertEquals(
            LocalDate.of(2016, 10, 1),
            item.getNearestSeasonDay(LocalDate.of(2016, 10, 1))
        )
        assertEquals(
            LocalDate.of(2016, 10, 19),
            item.getNearestSeasonDay(LocalDate.of(2016, 10, 19))
        )

        assertEquals(
            LocalDate.of(2016, 10, 1),
            item.getNearestSeasonDay(LocalDate.of(2016, 9, 1))
        )

        // TODO Use kluent
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = MonthDay.of(12, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        assertEquals(
            LocalDate.of(2016, 12, 15),
            item.getNearestSeasonDay(LocalDate.of(2016, 7, 14))
        )
        assertEquals(
            LocalDate.of(2016, 3, 10),
            item.getNearestSeasonDay(LocalDate.of(2016, 3, 10))
        )
    }

    @Test
    fun testGetNearestSeasonStart() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertNull(item.getNearestSeasonStart(LocalDate.of(2016, 2, 14)))
        assertNull(item.getNearestSeasonStart(LocalDate.of(2016, 3, 14)))

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        assertEquals(
            LocalDate.of(2016, 2, 15),
            item.getNearestSeasonStart(LocalDate.of(2016, 2, 14))
        )
        assertEquals(
            LocalDate.of(2017, 2, 15),
            item.getNearestSeasonStart(LocalDate.of(2016, 3, 14))
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30),
            startDay2 = MonthDay.of(10, 1),
            endDay2 = MonthDay.of(12, 31)
        )
        assertEquals(
            LocalDate.of(2016, 2, 15),
            item.getNearestSeasonStart(LocalDate.of(2016, 2, 14))
        )
        assertEquals(
            LocalDate.of(2016, 10, 1),
            item.getNearestSeasonStart(LocalDate.of(2016, 3, 14))
        )
        assertEquals(
            LocalDate.of(2016, 10, 1),
            item.getNearestSeasonStart(LocalDate.of(2016, 9, 13))
        )

        // check days equal as compare days
        assertEquals(
            LocalDate.of(2016, 10, 1),
            item.getNearestSeasonStart(LocalDate.of(2016, 10, 1))
        )
        assertEquals(
            LocalDate.of(2016, 2, 15),
            item.getNearestSeasonStart(LocalDate.of(2016, 2, 15))
        )

        //check when date is after second season start
        assertEquals(
            LocalDate.of(2017, 2, 15),
            item.getNearestSeasonStart(LocalDate.of(2016, 10, 15))
        )

        // check when end date is before start date
        item = FoodItem(
            true,
            "BRUKSELKA",
            startDay1 = MonthDay.of(10, 1),
            endDay1 = MonthDay.of(3, 15)
        )
        assertEquals(
            LocalDate.of(2016, 10, 1),
            item.getNearestSeasonStart(LocalDate.of(2016, 10, 1))
        )
        assertEquals(
            LocalDate.of(2017, 10, 1),
            item.getNearestSeasonStart(LocalDate.of(2016, 10, 10))
        )
    }

    @Test
    fun testGetNearestSeasonEnd() {
        // check all year items
        var item = FoodItem(true, "WHATEVER")
        assertNull(item.getNearestSeasonEnd(LocalDate.of(2016, 2, 14)))
        assertNull(item.getNearestSeasonEnd(LocalDate.of(2016, 5, 14)))

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        assertEquals(
            LocalDate.of(2016, 4, 30),
            item.getNearestSeasonEnd(LocalDate.of(2016, 2, 14))
        )
        assertEquals(
            LocalDate.of(2017, 4, 30),
            item.getNearestSeasonEnd(LocalDate.of(2016, 5, 14))
        )

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30),
            startDay2 = MonthDay.of(10, 1),
            endDay2 = MonthDay.of(12, 10)
        )
        assertEquals(
            LocalDate.of(2016, 4, 30),
            item.getNearestSeasonEnd(LocalDate.of(2016, 2, 14))
        )
        assertEquals(
            LocalDate.of(2016, 12, 10),
            item.getNearestSeasonEnd(LocalDate.of(2016, 5, 14))
        )
        assertEquals(
            LocalDate.of(2016, 12, 10),
            item.getNearestSeasonEnd(LocalDate.of(2016, 9, 13))
        )
        assertEquals(
            LocalDate.of(2017, 4, 30),
            item.getNearestSeasonEnd(LocalDate.of(2016, 12, 13))
        )

        // check days equal as compare days
        assertEquals(
            LocalDate.of(2016, 12, 10),
            item.getNearestSeasonEnd(LocalDate.of(2016, 12, 10))
        )
        assertEquals(
            LocalDate.of(2016, 4, 30),
            item.getNearestSeasonEnd(LocalDate.of(2016, 4, 30))
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