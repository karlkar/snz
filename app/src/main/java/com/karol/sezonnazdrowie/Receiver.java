package com.karol.sezonnazdrowie;

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
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String type = bundle.getString("type");
		int reqCode = bundle.getInt("reqCode");
		String title = bundle.getString("title");
		String text = bundle.getString("text");
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentTitle(title);
		builder.setContentText(text);
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setAutoCancel(true);
		builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));

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

		notiMgr.notify(type.equals("start") ? 0 : 1, builder.build());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.HOUR, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }
}
