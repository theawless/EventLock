package com.gobbledygook.theawless.eventlock.receivers.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.joda.time.DateTime;


public abstract class Alarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(getClass().getSimpleName(), "Received alarm");
        startWakefulService(context, new Intent(context, getServiceClass()).putExtras(intent));
    }

    public void setAlarm(Context context, long setTimeMillis, Intent intent) {
        Log.v(getClass().getSimpleName(), "Alarm at" + setTimeMillis + " " + new DateTime(setTimeMillis).toString("dd/MM/YYYY HH:mm:ss"));
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, getClass()).putExtras(intent), 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, setTimeMillis, alarmIntent);
        }
    }

    protected abstract Class getServiceClass();
}
