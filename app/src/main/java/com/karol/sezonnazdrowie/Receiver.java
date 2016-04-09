package com.karol.sezonnazdrowie;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.AlarmManager;
import java.util.Calendar;
import android.app.TaskStackBuilder;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String type = bundle.getString("type");
		String title = bundle.getString("title");
		String text = bundle.getString("text");
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle(title);
		builder.setContentText(text);
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setAutoCancel(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			builder.setStyle(new Notification.BigTextStyle().bigText(text));

		Intent notiIntent = new Intent(context, FragmentsActivity.class);
		notiIntent.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_INCOMING);
		PendingIntent pIntent;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(FragmentsActivity.class);
			stackBuilder.addNextIntent(notiIntent);
			pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		} else
			pIntent = PendingIntent.getActivity(context, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pIntent);

		NotificationManager	notiMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			notiMgr.notify(type.equals("start") ? 0 : 1, builder.build());
		else
			notiMgr.notify(type.equals("start") ? 0 : 1, builder.getNotification());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.HOUR, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }
}
