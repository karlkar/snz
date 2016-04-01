package com.karol.sezonnazdrowie;

import android.content.Context;
import com.prolificinteractive.materialcalendarview.*;
import java.util.ArrayList;

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

	public ArrayList<FoodItem> getIncomingItems() {
		ArrayList<FoodItem> list = new ArrayList<FoodItem>();
		CalendarDay today = CalendarDay.today();
		for (FoodItem item : getAllFruits()) {
			if (!item.existsAt(today)) {
				CalendarDay startDay1 = item.getStartDay1();
				CalendarDay startDay2 = item.getStartDay2();
				if (startDay1.isAfter(today) && startDay1.getMonth() - today.getMonth() <= 1) {
					list.add(item);
				} else if (startDay2 != null && startDay2.isAfter(today) && startDay2.getMonth() - today.getMonth() <= 1) {
					list.add(item);
				}
			}
		}
		for (FoodItem item : getAllVegetables()) {
			if (!item.existsAt(today)) {
				CalendarDay startDay1 = item.getStartDay1();
				CalendarDay startDay2 = item.getStartDay2();
				if (startDay1.isAfter(today) && startDay1.getMonth() - today.getMonth() <= 1) {
					list.add(item);
				} else if (startDay2 != null && startDay2.isAfter(today) && startDay2.getMonth() - today.getMonth() <= 1) {
					list.add(item);
				}
			}
		}

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
