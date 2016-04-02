package com.karol.sezonnazdrowie;

import android.content.Context;

import com.prolificinteractive.materialcalendarview.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by Karol on 16.03.2016.
 */
public class Database {

    private static final long INCOMING_SEASON_DAYS_DIFF = 30;

    private ArrayList<FoodItem> mFruits = null;
    private ArrayList<FoodItem> mVegetables = null;
    private ArrayList<FoodItem> mCurrentFruits = null;
    private ArrayList<FoodItem> mCurrentVegetables = null;

    private Database() {
    }

    public static Database getInstance() {
        return Holder.instance;
    }

    public ArrayList<FoodItem> getCurrentFruits() {
        if (mCurrentFruits != null)
            return mCurrentFruits;
        CalendarDay today = CalendarDay.today();
        mCurrentFruits = new ArrayList<>();
        for (FoodItem item : mFruits) {
            if (item.existsAt(today))
                mCurrentFruits.add(item);
        }
        return mCurrentFruits;
    }

    public ArrayList<FoodItem> getCurrentVegetables() {
        if (mCurrentVegetables != null)
            return mCurrentVegetables;
        CalendarDay today = CalendarDay.today();
        mCurrentVegetables = new ArrayList<>();
        for (FoodItem item : mVegetables) {
            if (item.existsAt(today))
                mCurrentVegetables.add(item);
        }

        return mCurrentVegetables;
    }

    public ArrayList<FoodItem> getIncomingItems() {
        ArrayList<FoodItem> list = new ArrayList<>();
        CalendarDay today = CalendarDay.today();
        Calendar todayDate = Calendar.getInstance();
        todayDate.set(Calendar.YEAR, 0);
        Calendar comparisonDay = Calendar.getInstance();
        for (FoodItem item : getAllFruits()) {
            if (!item.existsAt(today)) {
                CalendarDay startDay1 = item.getStartDay1();
                comparisonDay.set(0, startDay1.getMonth(), startDay1.getDay());

                if (comparisonDay.after(todayDate) && TimeUnit.DAYS.convert(
                        comparisonDay.getTimeInMillis() - todayDate.getTimeInMillis(),
                        TimeUnit.MILLISECONDS) < INCOMING_SEASON_DAYS_DIFF)
                    list.add(item);
                else {
                    CalendarDay startDay2 = item.getStartDay2();
                    if (startDay2 != null) {
                        comparisonDay.set(0, startDay2.getMonth(), startDay2.getDay());
                        if (comparisonDay.after(todayDate) && TimeUnit.DAYS.convert(
                                comparisonDay.getTimeInMillis() - todayDate.getTimeInMillis(),
                                TimeUnit.MILLISECONDS) < INCOMING_SEASON_DAYS_DIFF)
                        list.add(item);
                    }
                }
            }
        }
        for (FoodItem item : getAllVegetables()) {
            if (!item.existsAt(today)) {
                CalendarDay startDay1 = item.getStartDay1();
                comparisonDay.set(0, startDay1.getMonth(), startDay1.getDay());

                if (comparisonDay.after(todayDate) && TimeUnit.DAYS.convert(
                        comparisonDay.getTimeInMillis() - todayDate.getTimeInMillis(),
                        TimeUnit.MILLISECONDS) < INCOMING_SEASON_DAYS_DIFF)
                    list.add(item);
                else {
                    CalendarDay startDay2 = item.getStartDay2();
                    if (startDay2 != null) {
                        comparisonDay.set(0, startDay2.getMonth(), startDay2.getDay());
                        if (comparisonDay.after(todayDate) && TimeUnit.DAYS.convert(
                                comparisonDay.getTimeInMillis() - todayDate.getTimeInMillis(),
                                TimeUnit.MILLISECONDS) < INCOMING_SEASON_DAYS_DIFF)
                            list.add(item);
                    }
                }
            }
        }

        Collections.sort(list);
        return list;
    }

    public void loadData(Context ctx) {
        Holder.instance.mFruits = FoodItem.createItems(ctx, R.raw.fruits, true);
        Holder.instance.mVegetables = FoodItem.createItems(ctx, R.raw.vegetables, false);
    }

    public ArrayList<FoodItem> getAllFruits() {
        return mFruits;
    }

    public ArrayList<FoodItem> getAllVegetables() {
        return mVegetables;
    }

    private static class Holder {
        static final Database instance = new Database();
    }
}
