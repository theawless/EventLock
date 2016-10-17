package com.gobbledygook.theawless.eventlock.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


public class AlarmWakefulBroadcastReceiver extends WakefulBroadcastReceiver {
    private final String TAG;
    private final Class classType;
    private PendingIntent alarmIntent;
    private AlarmManager alarmManager;

    AlarmWakefulBroadcastReceiver(Class classType) {
        super();
        this.TAG = classType.getSimpleName();
        this.classType = classType;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");
        Intent service = new Intent(context, classType);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context, long setTimeMillis) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CalendarLoaderAlarm.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, setTimeMillis, alarmIntent);
        Log.v(TAG, "alarm set");
    }
}
