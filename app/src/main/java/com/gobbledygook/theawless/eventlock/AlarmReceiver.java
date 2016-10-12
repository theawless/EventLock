package com.gobbledygook.theawless.eventlock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

//calls Scheduling Service after required time, then Scheduling service sets the alram again. LOOP.
public class AlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    PendingIntent alarmIntent;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");
        Intent service = new Intent(context, SchedulingService.class);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context, long setTimeMillis) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, setTimeMillis, alarmIntent);
        Log.v(TAG, "alarm set");
    }
}
