package com.karol.sezonnazdrowie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import android.content.*;
import android.os.*;

/**
 * Created by Karol on 03.04.2016.
 */
public class SnzAlarmManager {

    public static void setAlarms(Context ctx) {
        HashMap<CalendarDay, ArrayList<FoodItem>> startMap = new HashMap<>();
        HashMap<CalendarDay, ArrayList<FoodItem>> endMap = new HashMap<>();

        fillMapWithItems(startMap, endMap, Database.getInstance().getAllFruits());
        fillMapWithItems(startMap, endMap, Database.getInstance().getAllVegetables());

        ArrayList<Integer> startDays = new ArrayList<>();
        Set<String> seasonStart = PreferenceManager.getDefaultSharedPreferences(ctx).getStringSet("pref_season_start", null);
        if (seasonStart == null)
            startDays.add(7);
        else {
            if (seasonStart.contains(ctx.getString(R.string.at_the_start_day)))
                startDays.add(0);
            else if (seasonStart.contains(ctx.getString(R.string.week_before)))
                startDays.add(7);
            else if (seasonStart.contains(ctx.getString(R.string.month_before)))
                startDays.add(30);
        }

        Intent intent = new Intent(ctx, Receiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
		
        Calendar today = Calendar.getInstance();
        for (CalendarDay day : startMap.keySet()) {
			ArrayList<String> items = new ArrayList<>();
			for (FoodItem item : startMap.get(day)) {
				if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_noti_" + item.getName(), false))
					items.add(item.getName());
			}
			if (items.size() > 0) {
	            for (Integer dayDiff : startDays) {
					intent = new Intent(ctx, Receiver.class);
					intent.putExtra("type", "start");
					intent.putExtra("items", items);
					intent.putExtra("days", dayDiff);
					alarmIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
    	            Calendar calendar = day.getCalendar();
        	        calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
            	    calendar.add(Calendar.DAY_OF_MONTH, -dayDiff);
	                if (calendar.before(today))
	 	                calendar.add(Calendar.YEAR, 1);
	                calendar.set(Calendar.HOUR_OF_DAY, 8);
	                calendar.set(Calendar.MINUTE, 0);
	                calendar.set(Calendar.SECOND, 0);

	                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    	        }
			}
        }

        ArrayList<Integer> endDays = new ArrayList<>();
        Set<String> seasonEnd = PreferenceManager.getDefaultSharedPreferences(ctx).getStringSet("pref_season_end", null);
        if (seasonEnd == null)
            endDays.add(7);
        else {
            if (seasonEnd.contains(ctx.getString(R.string.at_the_end_day)))
                endDays.add(0);
            else if (seasonEnd.contains(ctx.getString(R.string.week_before)))
                endDays.add(7);
        }

        for (CalendarDay day : endMap.keySet()) {
			ArrayList<String> items = new ArrayList<>();
			for (FoodItem item : startMap.get(day)) {
				if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_noti_" + item.getName(), false))
					items.add(item.getName());
			}
			if (items.size() > 0) {
	            for (Integer dayDiff : endDays) {
					intent = new Intent(ctx, Receiver.class);
					intent.putExtra("type", "end");
					intent.putExtra("items", items);
					intent.putExtra("days", dayDiff);
					alarmIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
    	            Calendar calendar = day.getCalendar();
        	        calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
        	        calendar.add(Calendar.DAY_OF_MONTH, -dayDiff);
        	        if (calendar.before(today))
        	            calendar.add(Calendar.YEAR, 1);
        	        calendar.set(Calendar.HOUR_OF_DAY, 8);
        	        calendar.set(Calendar.MINUTE, 0);
        	        calendar.set(Calendar.SECOND, 0);
	
	                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
				}
            }
        }
    }

    private static void fillMapWithItems(HashMap<CalendarDay, ArrayList<FoodItem>> startMap,
                                  HashMap<CalendarDay, ArrayList<FoodItem>> endMap,
                                  ArrayList<FoodItem> items) {
        for (FoodItem item : items) {
            if (item.getStartDay1() == null)
                continue;

            if (startMap.containsKey(item.getStartDay1())) {
                startMap.get(item.getStartDay1()).add(item);
            } else {
                ArrayList<FoodItem> list = new ArrayList<>();
                list.add(item);
                startMap.put(item.getStartDay1(), list);
            }
            if (item.getStartDay2() != null) {
                if (startMap.containsKey(item.getStartDay2())) {
                    startMap.get(item.getStartDay2()).add(item);
                } else {
                    ArrayList<FoodItem> list = new ArrayList<>();
                    list.add(item);
                    startMap.put(item.getStartDay2(), list);
                }
            }

            if (endMap.containsKey(item.getEndDay1())) {
                endMap.get(item.getEndDay1()).add(item);
            } else {
                ArrayList<FoodItem> list = new ArrayList<>();
                list.add(item);
                endMap.put(item.getEndDay1(), list);
            }
            if (item.getEndDay2() != null) {
                if (endMap.containsKey(item.getEndDay2())) {
                    endMap.get(item.getEndDay2()).add(item);
                } else {
                    ArrayList<FoodItem> list = new ArrayList<>();
                    list.add(item);
                    endMap.put(item.getEndDay2(), list);
                }
            }
        }
    }
}
