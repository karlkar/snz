package com.karol.sezonnazdrowie;

import com.karol.sezonnazdrowie.data.FoodItem;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import junit.framework.TestCase;

public class FoodItemTest extends TestCase {

    public void testGetNearestSeasonDay() {
        // check all year items
        FoodItem item = new FoodItem("WHATEVER###############################".split("#", -1), true);
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)), CalendarDay.from(2016, 2, 14));
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 9, 30)), CalendarDay.from(2016, 9, 30));

        item = new FoodItem("WHATEVER###15.02#30.04###########################".split("#", -1), true);
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 1, 14)), CalendarDay.from(2016, 1, 15));
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)), CalendarDay.from(2016, 2, 14));

        //check items with two seasons
        item = new FoodItem("ROSZPONKA###15.02#30.04#1.10#31.12#########################".split("#", -1), true);
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 1, 14)), CalendarDay.from(2016, 1, 15));
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)), CalendarDay.from(2016, 2, 14));
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 5, 14)), CalendarDay.from(2016, 9, 1));
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 9, 1)), CalendarDay.from(2016, 9, 1));
        assertEquals(item.getNearestSeasonDay(CalendarDay.from(2016, 9, 19)), CalendarDay.from(2016, 9, 19));

        item = new FoodItem("ROSZPONKA###15.12#30.04#############################".split("#", -1), true);
        assertEquals("Equals", CalendarDay.from(2016, 11, 15), item.getNearestSeasonDay(CalendarDay.from(2016, 6, 14)));
        assertEquals("Equals", CalendarDay.from(2016, 2, 10), item.getNearestSeasonDay(CalendarDay.from(2016, 2, 10)));
    }

    public void testGetNearestSeasonStart() {
        // check all year items
        FoodItem item = new FoodItem("WHATEVER###############################".split("#", -1), true);
        assertNull(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)));
        assertNull(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)));

        // check items with one season
        item = new FoodItem("WHATEVER###15.02#30.04###########################".split("#", -1), true);
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)), CalendarDay.from(2016, 1, 15));
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)), CalendarDay.from(2017, 1, 15));

        //check items with two seasons
        item = new FoodItem("ROSZPONKA###15.02#30.04#1.10#31.12#########################".split("#", -1), true);
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)), CalendarDay.from(2016, 1, 15));
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)), CalendarDay.from(2016, 9, 1));
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 8, 13)), CalendarDay.from(2016, 9, 1));

        // check days equal as compare days
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 1)), CalendarDay.from(2016, 9, 1));
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 15)), CalendarDay.from(2016, 1, 15));

        //check when date is after second season start
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 15)), CalendarDay.from(2017, 1, 15));

        // check when end date is before start date
        item = new FoodItem("BRUKSELKA###1.10#15.03###########################".split("#", -1), true);
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 1)), CalendarDay.from(2016, 9, 1));
        assertEquals(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 10)), CalendarDay.from(2017, 9, 1));
    }

    public void testGetNearestSeasonEnd() {
        // check all year items
        FoodItem item = new FoodItem("WHATEVER###############################".split("#", -1), true);
        assertNull(item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)));
        assertNull(item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)));

        // check items with one season
        item = new FoodItem("WHATEVER###15.02#30.04###########################".split("#", -1), true);
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)), CalendarDay.from(2016, 3, 30));
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)), CalendarDay.from(2017, 3, 30));

        //check items with two seasons
        item = new FoodItem("ROSZPONKA###15.02#30.04#1.10#10.12#########################".split("#", -1), true);
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)), CalendarDay.from(2016, 3, 30));
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)), CalendarDay.from(2016, 11, 10));
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 8, 13)), CalendarDay.from(2016, 11, 10));
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 11, 13)), CalendarDay.from(2017, 3, 30));

        // check days equal as compare days
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 11, 10)), CalendarDay.from(2016, 11, 10));
        assertEquals(item.getNearestSeasonEnd(CalendarDay.from(2016, 3, 30)), CalendarDay.from(2016, 3, 30));
    }
}