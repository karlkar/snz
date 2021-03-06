package com.karol.sezonnazdrowie

import com.karol.sezonnazdrowie.data.FoodItem
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`

import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.MonthDay

class FoodItemTest {

    @Test
    fun testGetNearestSeasonDay() {
        // check all year items
        var item = FoodItem(true, "WHATEVER", "whatever", "image")
        item.getNearestSeasonDay(LocalDate.of(2016, 3, 14)) `should equal`
                LocalDate.of(2016, 3, 14)
        item.getNearestSeasonDay(LocalDate.of(2016, 10, 30)) `should equal`
                LocalDate.of(2016, 10, 30)

        item = FoodItem(
            true,
            "WHATEVER",
            "whatever",
            "image",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        item.getNearestSeasonDay(LocalDate.of(2016, 2, 14)) `should equal`
                LocalDate.of(2016, 2, 15)
        item.getNearestSeasonDay(LocalDate.of(2016, 3, 14)) `should equal`
                LocalDate.of(2016, 3, 14)

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            "whatever",
            "image",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30),
            startDay2 = MonthDay.of(10, 1),
            endDay2 = MonthDay.of(1, 10)
        )
        item.getNearestSeasonDay(LocalDate.of(2016, 2, 14)) `should equal`
                LocalDate.of(2016, 2, 15)
        item.getNearestSeasonDay(LocalDate.of(2016, 3, 14)) `should equal`
                LocalDate.of(2016, 3, 14)
        item.getNearestSeasonDay(LocalDate.of(2016, 6, 14)) `should equal`
                LocalDate.of(2016, 10, 1)
        item.getNearestSeasonDay(LocalDate.of(2016, 10, 1)) `should equal`
                LocalDate.of(2016, 10, 1)
        item.getNearestSeasonDay(LocalDate.of(2016, 10, 19)) `should equal`
                LocalDate.of(2016, 10, 19)
        item.getNearestSeasonDay(LocalDate.of(2016, 9, 1)) `should equal`
                LocalDate.of(2016, 10, 1)

        item = FoodItem(
            true,
            "ROSZPONKA",
            "whatever",
            "image",
            startDay1 = MonthDay.of(12, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        item.getNearestSeasonDay(LocalDate.of(2016, 7, 14)) `should equal`
                LocalDate.of(2016, 12, 15)
        item.getNearestSeasonDay(LocalDate.of(2016, 3, 10)) `should equal`
                LocalDate.of(2016, 3, 10)
    }

    @Test
    fun testGetNearestSeasonStart() {
        // check all year items
        var item = FoodItem(true, "WHATEVER", "whatever", "image")
        item.getNearestSeasonStart(LocalDate.of(2016, 2, 14)) `should be` null
        item.getNearestSeasonStart(LocalDate.of(2016, 3, 14)) `should be` null

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            "whatever",
            "image",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        item.getNearestSeasonStart(LocalDate.of(2016, 2, 14)) `should equal`
                LocalDate.of(2016, 2, 15)
        item.getNearestSeasonStart(LocalDate.of(2016, 3, 14)) `should equal`
                LocalDate.of(2017, 2, 15)

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            "whatever",
            "image",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30),
            startDay2 = MonthDay.of(10, 1),
            endDay2 = MonthDay.of(12, 31)
        )
        item.getNearestSeasonStart(LocalDate.of(2016, 2, 14)) `should equal`
                LocalDate.of(2016, 2, 15)
        item.getNearestSeasonStart(LocalDate.of(2016, 3, 14)) `should equal`
                LocalDate.of(2016, 10, 1)
        item.getNearestSeasonStart(LocalDate.of(2016, 9, 13)) `should equal`
                LocalDate.of(2016, 10, 1)

        // check days equal as compare days
        item.getNearestSeasonStart(LocalDate.of(2016, 10, 1)) `should equal`
                LocalDate.of(2016, 10, 1)
        item.getNearestSeasonStart(LocalDate.of(2016, 2, 15)) `should equal`
                LocalDate.of(2016, 2, 15)

        //check when date is after second season start
        item.getNearestSeasonStart(LocalDate.of(2016, 10, 15)) `should equal`
                LocalDate.of(2017, 2, 15)

        // check when end date is before start date
        item = FoodItem(
            true,
            "BRUKSELKA",
            "whatever",
            "image",
            startDay1 = MonthDay.of(10, 1),
            endDay1 = MonthDay.of(3, 15)
        )
        item.getNearestSeasonStart(LocalDate.of(2016, 10, 1)) `should equal`
                LocalDate.of(2016, 10, 1)
        item.getNearestSeasonStart(LocalDate.of(2016, 10, 10)) `should equal`
                LocalDate.of(2017, 10, 1)
    }

    @Test
    fun testGetNearestSeasonEnd() {
        // check all year items
        var item = FoodItem(true, "WHATEVER", "whatever", "image")
        item.getNearestSeasonEnd(LocalDate.of(2016, 2, 14)) `should be` null
        item.getNearestSeasonEnd(LocalDate.of(2016, 5, 14)) `should be` null

        // check items with one season
        item = FoodItem(
            true,
            "WHATEVER",
            "whatever",
            "image",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30)
        )
        item.getNearestSeasonEnd(LocalDate.of(2016, 2, 14)) `should equal`
                LocalDate.of(2016, 4, 30)
        item.getNearestSeasonEnd(LocalDate.of(2016, 5, 14)) `should equal`
                LocalDate.of(2017, 4, 30)

        //check items with two seasons
        item = FoodItem(
            true,
            "ROSZPONKA",
            "whatever",
            "image",
            startDay1 = MonthDay.of(2, 15),
            endDay1 = MonthDay.of(4, 30),
            startDay2 = MonthDay.of(10, 1),
            endDay2 = MonthDay.of(12, 10)
        )
        item.getNearestSeasonEnd(LocalDate.of(2016, 2, 14)) `should equal`
                LocalDate.of(2016, 4, 30)
        item.getNearestSeasonEnd(LocalDate.of(2016, 5, 14)) `should equal`
                LocalDate.of(2016, 12, 10)
        item.getNearestSeasonEnd(LocalDate.of(2016, 9, 13)) `should equal`
                LocalDate.of(2016, 12, 10)
        item.getNearestSeasonEnd(LocalDate.of(2016, 12, 13)) `should equal`
                LocalDate.of(2017, 4, 30)

        // check days equal as compare days
        item.getNearestSeasonEnd(LocalDate.of(2016, 12, 10)) `should equal`
                LocalDate.of(2016, 12, 10)
        item.getNearestSeasonEnd(LocalDate.of(2016, 4, 30)) `should equal`
                LocalDate.of(2016, 4, 30)
    }

    @Test
    fun `given has any of proximates should return true when asked if has proximates`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", "whatever", "image", water = "2mg")

        // when
        val hasProximates = foodItem.hasProximates()

        // then
        hasProximates `should be` true
    }

    @Test
    fun `given has no proximates should return false when asked if has proximates`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", "whatever", "image")

        // when
        val hasProximates = foodItem.hasProximates()

        // then
        hasProximates `should be` false
    }

    @Test
    fun `given has any of minerals should return true when asked if has minerals`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", "whatever", "image", calcium = "2mg")

        // when
        val hasMinerals = foodItem.hasMinerals()

        // then
        hasMinerals `should be` true
    }

    @Test
    fun `given has no minerals should return false when asked if has minerals`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", "whatever", "image")

        // when
        val hasMinerals = foodItem.hasMinerals()

        // then
        hasMinerals `should be` false
    }

    @Test
    fun `given has any of vitamins should return true when asked if has vitamins`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", "whatever", "image", vitA = "2mg")

        // when
        val hasVitamins = foodItem.hasVitamins()

        // then
        hasVitamins `should be` true
    }

    @Test
    fun `given has no vitamins should return false when asked if has vitamins`() {
        // given
        val foodItem = FoodItem(false, "WHATEVER", "whatever", "image")

        // when
        val hasVitamins = foodItem.hasVitamins()

        // then
        hasVitamins `should be` false
    }
}