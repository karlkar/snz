package com.karol.sezonnazdrowie.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.data.Database;
import com.karol.sezonnazdrowie.data.FoodItem;
import com.karol.sezonnazdrowie.view.controls.TimePreference;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SnzAlarmManager {

    private final static String TAG = "SNZALARMMANAGER";

    public static void startSetAlarmsTask(final Context ctx, final Database database) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SnzAlarmManager.setAlarms(ctx, database);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    static void setAlarms(Context ctx, Database database) {
        HashMap<CalendarDay, ArrayList<FoodItem>> startMap = new HashMap<>();
        HashMap<CalendarDay, ArrayList<FoodItem>> endMap = new HashMap<>();

        fillMapWithItems(startMap, endMap, database.getAllFruits());
        fillMapWithItems(startMap, endMap, database.getAllVegetables());

        ArrayList<Integer> startDays = new ArrayList<>();
        Set<String> seasonStart = PreferenceManager.getDefaultSharedPreferences(ctx).getStringSet("pref_season_start", null);
        if (seasonStart == null)
            startDays.add(7);
        else {
            if (seasonStart.contains(ctx.getString(R.string.at_the_start_day)))
                startDays.add(0);
            if (seasonStart.contains(ctx.getString(R.string.week_before)))
                startDays.add(7);
            if (seasonStart.contains(ctx.getString(R.string.month_before)))
                startDays.add(30);
        }

        Intent intent;
        PendingIntent alarmIntent;
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        int reqCode = 1;
        String notiTime = PreferenceManager.getDefaultSharedPreferences(ctx).getString("pref_notification_hour", "20:00");
        int notiHour = TimePreference.getHour(notiTime);
        int notiMinute = TimePreference.getMinute(notiTime);

        Calendar today = Calendar.getInstance();
        for (CalendarDay day : startMap.keySet()) {
			StringBuilder strBuilder = new StringBuilder();
			for (FoodItem item : startMap.get(day)) {
				if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_noti_" + item.getName(), true)) {
					if (strBuilder.length() > 0)
						strBuilder.append(", ");
					strBuilder.append(item.getConjugatedName());
				}
			}
			if (strBuilder.length() > 0) {
	            for (Integer dayDiff : startDays) {
					String title = "";
					if (dayDiff == 0)
						title = ctx.getString(R.string.season_starts_soon);
					else if (dayDiff == 7)
						title = ctx.getString(R.string.season_starts_week);
					else if (dayDiff == 30)
						title = ctx.getString(R.string.season_starts_month);
					intent = new Intent(ctx, Receiver.class);
					intent.putExtra("type", "start");
                    intent.putExtra("reqCode", reqCode);
					intent.putExtra("title", title);
					intent.putExtra("text", strBuilder.toString());
					alarmIntent = PendingIntent.getBroadcast(ctx, reqCode++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	            Calendar calendar = (Calendar) day.getCalendar().clone();
        	        calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
            	    calendar.add(Calendar.DAY_OF_MONTH, -dayDiff);
	                if (calendar.before(today))
	 	                calendar.add(Calendar.YEAR, 1);
	                calendar.set(Calendar.HOUR_OF_DAY, notiHour);
	                calendar.set(Calendar.MINUTE, notiMinute);
	                calendar.set(Calendar.SECOND, 0);

	                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                    Log.d(TAG, "setAlarms: Start Alarm set @ " + calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH));
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
            if (seasonEnd.contains(ctx.getString(R.string.week_before)))
                endDays.add(7);
        }

        for (CalendarDay day : endMap.keySet()) {
			StringBuilder strBuilder = new StringBuilder();
			for (FoodItem item : endMap.get(day)) {
                if (PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_noti_" + item.getName(), true)) {
					if (strBuilder.length() > 0)
						strBuilder.append(", ");
					strBuilder.append(item.getConjugatedName());
				} else
                    Log.d(TAG, "setAlarms: Skipping the item " + item.getName());
			}
			if (strBuilder.length() > 0) {
	            for (Integer dayDiff : endDays) {
					String title = "";
					if (dayDiff == 0)
						title = ctx.getString(R.string.season_ends_soon);
					else if (dayDiff == 7)
						title = ctx.getString(R.string.season_ends_week);
					intent = new Intent(ctx, Receiver.class);
					intent.putExtra("type", "end");
                    intent.putExtra("reqCode", reqCode);
					intent.putExtra("title", title);
					intent.putExtra("text", strBuilder.toString());
					alarmIntent = PendingIntent.getBroadcast(ctx, reqCode++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	            Calendar calendar = (Calendar) day.getCalendar().clone();
        	        calendar.set(Calendar.YEAR, today.get(Calendar.YEAR));
        	        calendar.add(Calendar.DAY_OF_MONTH, -dayDiff);
        	        if (calendar.before(today))
        	            calendar.add(Calendar.YEAR, 1);
        	        calendar.set(Calendar.HOUR_OF_DAY, notiHour);
        	        calendar.set(Calendar.MINUTE, notiMinute);
        	        calendar.set(Calendar.SECOND, 0);
	
	                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                    Log.d(TAG, "setAlarms: End Alarm set @ " + calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH));
				}
            }
        }
        int prevMaxReqCode = PreferenceManager.getDefaultSharedPreferences(ctx).getInt("maxReqCode", 0);
        Log.d(TAG, "setAlarms: Ending with " + reqCode + ", previous ending " + prevMaxReqCode);
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putInt("maxReqCode", reqCode).apply();

        intent = new Intent(ctx, Receiver.class);
        while (prevMaxReqCode > reqCode) {
            Log.d(TAG, "setAlarms: Cancelling an alarm of reqCode = " + reqCode);
            alarmIntent = PendingIntent.getBroadcast(ctx, reqCode++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(alarmIntent);
        }
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("pref_alarms_set", true).apply();
    }

    private static void fillMapWithItems(
            HashMap<CalendarDay, ArrayList<FoodItem>> startMap,
            HashMap<CalendarDay, ArrayList<FoodItem>> endMap,
            List<FoodItem> items) {
        for (FoodItem item : items) {
            if (item.getStartDay1() == null)
                continue;

            if (startMap.containsKey(item.getStartDay1()))
                startMap.get(item.getStartDay1()).add(item);
            else {
                ArrayList<FoodItem> list = new ArrayList<>();
                list.add(item);
                startMap.put(item.getStartDay1(), list);
            }
            if (item.getStartDay2() != null) {
                if (startMap.containsKey(item.getStartDay2()))
                    startMap.get(item.getStartDay2()).add(item);
                else {
                    ArrayList<FoodItem> list = new ArrayList<>();
                    list.add(item);
                    startMap.put(item.getStartDay2(), list);
                }
            }

            if (endMap.containsKey(item.getEndDay1()))
                endMap.get(item.getEndDay1()).add(item);
            else {
                ArrayList<FoodItem> list = new ArrayList<>();
                list.add(item);
                endMap.put(item.getEndDay1(), list);
            }
            if (item.getEndDay2() != null) {
                if (endMap.containsKey(item.getEndDay2()))
                    endMap.get(item.getEndDay2()).add(item);
                else {
                    ArrayList<FoodItem> list = new ArrayList<>();
                    list.add(item);
                    endMap.put(item.getEndDay2(), list);
                }
            }
        }
    }
}
