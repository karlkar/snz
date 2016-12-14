package com.karol.sezonnazdrowie;

import com.karol.sezonnazdrowie.data.FoodItem;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import junit.framework.TestCase;

public class FoodItemTest extends TestCase {

    public void testGetNearestSeasonDay() throws Exception {
        // check all year items
        FoodItem item = new FoodItem("WHATEVER###############################".split("#", -1), true);
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)).equals(CalendarDay.from(2016, 2, 14)));
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 9, 30)).equals(CalendarDay.from(2016, 9, 30)));

        item = new FoodItem("WHATEVER###15.02#30.04###########################".split("#", -1), true);
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 1, 14)).equals(CalendarDay.from(2016, 1, 15)));
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)).equals(CalendarDay.from(2016, 2, 14)));

        //check items with two seasons
        item = new FoodItem("ROSZPONKA###15.02#30.04#1.10#31.12#########################".split("#", -1), true);
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 1, 14)).equals(CalendarDay.from(2016, 1, 15)));
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 2, 14)).equals(CalendarDay.from(2016, 2, 14)));
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 5, 14)).equals(CalendarDay.from(2016, 9, 1)));
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 9, 1)).equals(CalendarDay.from(2016, 9, 1)));
        assertTrue(item.getNearestSeasonDay(CalendarDay.from(2016, 9, 19)).equals(CalendarDay.from(2016, 9, 19)));
    }

    public void testGetNearestSeasonStart() throws Exception {
        // check all year items
        FoodItem item = new FoodItem("WHATEVER###############################".split("#", -1), true);
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)) == null);
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)) == null);

        // check items with one season
        item = new FoodItem("WHATEVER###15.02#30.04###########################".split("#", -1), true);
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)).equals(CalendarDay.from(2016, 1, 15)));
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)).equals(CalendarDay.from(2017, 1, 15)));

        //check items with two seasons
        item = new FoodItem("ROSZPONKA###15.02#30.04#1.10#31.12#########################".split("#", -1), true);
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 14)).equals(CalendarDay.from(2016, 1, 15)));
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 2, 14)).equals(CalendarDay.from(2016, 9, 1)));
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 8, 13)).equals(CalendarDay.from(2016, 9, 1)));

        // check days equal as compare days
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 1)).equals(CalendarDay.from(2016, 9, 1)));
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 1, 15)).equals(CalendarDay.from(2016, 1, 15)));

        //check when date is after second season start
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 15)).equals(CalendarDay.from(2017, 1, 15)));

        // check when end date is before start date
        item = new FoodItem("BRUKSELKA###1.10#15.03###########################".split("#", -1), true);
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 1)).equals(CalendarDay.from(2016, 9, 1)));
        assertTrue(item.getNearestSeasonStart(CalendarDay.from(2016, 9, 10)).equals(CalendarDay.from(2017, 9, 1)));
    }

    public void testGetNearestSeasonEnd() throws Exception {
        // check all year items
        FoodItem item = new FoodItem("WHATEVER###############################".split("#", -1), true);
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)) == null);
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)) == null);

        // check items with one season
        item = new FoodItem("WHATEVER###15.02#30.04###########################".split("#", -1), true);
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)).equals(CalendarDay.from(2016, 3, 30)));
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)).equals(CalendarDay.from(2017, 3, 30)));

        //check items with two seasons
        item = new FoodItem("ROSZPONKA###15.02#30.04#1.10#10.12#########################".split("#", -1), true);
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 1, 14)).equals(CalendarDay.from(2016, 3, 30)));
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 4, 14)).equals(CalendarDay.from(2016, 11, 10)));
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 8, 13)).equals(CalendarDay.from(2016, 11, 10)));
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 11, 13)).equals(CalendarDay.from(2017, 3, 30)));

        // check days equal as compare days
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 11, 10)).equals(CalendarDay.from(2016, 11, 10)));
        assertTrue(item.getNearestSeasonEnd(CalendarDay.from(2016, 3, 30)).equals(CalendarDay.from(2016, 3, 30)));
    }
}