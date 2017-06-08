package com.karol.sezonnazdrowie.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.karol.sezonnazdrowie.data.Database;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Database.getInstance().loadData(context);
            SnzAlarmManager.setAlarms(context);
        }
    }
}
