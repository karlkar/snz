package com.karol.sezonnazdrowie;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.*;
import android.os.*;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String type = bundle.getString("type");
		int days = bundle.getInt("days");
		String title = "";
		if (type.equals("start")) {
			if (days == 0)
				title = "Wkrótce sezon na";
			else if (days == 7)
				title = "Za tydzień zacznie się sezon na";
			else if (days == 30)
				title = "Za miesiąc zacznie się sezon na";
		} else {
			if (days == 0)
				title = "Wkrótce skończy się sezon na";
			else if (days == 7)
				title = "Za tydzień skończy się sezon na";
		}
		
		StringBuilder strBuilder = new StringBuilder();
		for (String str : bundle.getStringArrayList("items")) {
			if (strBuilder.length() > 0)
				strBuilder.append(", ");
			strBuilder.append(str);
		}
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle(title);
		builder.setContentText(strBuilder.toString());
		
		NotificationManager	notiMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notiMgr.notify(type.equals("start") ? 0 : 1, builder.build());
		//TODO: Reschedule the alarm to be called in one year again
    }
}
