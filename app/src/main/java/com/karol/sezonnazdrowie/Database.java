package com.karol.sezonnazdrowie;

import android.content.Context;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Karol on 16.03.2016.
 */
public class Database {

    private ArrayList<FoodItem> mFruits = null;
    private ArrayList<FoodItem> mVegetables = null;
    private ArrayList<FoodItem> mCurrentFruits = null;
    private ArrayList<FoodItem> mCurrentVegetables = null;

    public static Database getInstance() {
        return Holder.instance;
    }

    private Database() {
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
