package com.karol.sezonnazdrowie.data;

import android.content.Context;
import android.preference.PreferenceManager;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.model.SnzAlarmManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class Database {

    private static final long INCOMING_SEASON_DAYS_DIFF = 30;

    private static Database sInstance = null;

    private ArrayList<FoodItem> mFruits = new ArrayList<>();
    private ArrayList<FoodItem> mVegetables = new ArrayList<>();
    private ArrayList<FoodItem> mCurrentFruits = null;
    private ArrayList<FoodItem> mCurrentVegetables = null;

    private Database() {
    }

    public static Database getInstance() {
        if (sInstance == null)
            sInstance = new Database();
        return sInstance;
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
        for (FoodItem item : getAllFruits()) {
            if (!item.existsAt(today)) {
                CalendarDay startDay1 = item.getStartDay1();

                long daysDiff = startDay1.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF)
                    list.add(item);
                else {
                    CalendarDay startDay2 = item.getStartDay2();
                    if (startDay2 != null) {
                        daysDiff = startDay2.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                        if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF)
                            list.add(item);
                    }
                }
            }
        }
        for (FoodItem item : getAllVegetables()) {
            if (!item.existsAt(today)) {
                CalendarDay startDay1 = item.getStartDay1();

                long daysDiff = startDay1.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF)
                    list.add(item);
                else {
                    CalendarDay startDay2 = item.getStartDay2();
                    if (startDay2 != null) {
                        daysDiff = startDay2.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                        if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF)
                            list.add(item);
                    }
                }
            }
        }

        Collections.sort(list, new Comparator<FoodItem>() {
			@Override
			public int compare(FoodItem lhs, FoodItem rhs) {
        	    CalendarDay today = CalendarDay.today();
        	    CalendarDay lhsDay = lhs.getNearestSeasonStart(today);
        	    CalendarDay rhsDay = rhs.getNearestSeasonStart(today);
				if (lhsDay == null && rhsDay != null)
					return -1;
				if (lhsDay != null && rhsDay == null)
					return 1;
				if (lhsDay == null && rhsDay == null || lhsDay.equals(rhsDay))
					return 0;
				return lhsDay.isBefore(rhsDay) ? -1 : 1;
			}
		});
        return list;
    }

    public void loadData(Context ctx) {
        sInstance.mFruits = FoodItem.createItems(ctx, com.karol.sezonnazdrowie.R.raw.fruits, true);
        sInstance.mVegetables = FoodItem.createItems(ctx, R.raw.vegetables, false);
		
		boolean alarmsSet = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_alarms_set", false);
		if (!alarmsSet)
			SnzAlarmManager.startSetAlarmsTask(ctx);
    }

    public ArrayList<FoodItem> getAllFruits() {
        return mFruits;
    }

    public ArrayList<FoodItem> getAllVegetables() {
        return mVegetables;
    }
}
