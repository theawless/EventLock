package com.gobbledygook.theawless.eventlock.receivers.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gobbledygook.theawless.eventlock.services.CurrentEventUpdaterService;

import org.joda.time.DateTime;

public class CurrentEventUpdaterAlarm extends Alarm {
    @Override
    protected Class getServiceClass() {
        return CurrentEventUpdaterService.class;
    }

    public void setAlarm(Context context, long setTimeMillis, long[] beginTimes, long[] endTimes) {
        Log.v("Asdf", "alarm at" + setTimeMillis + new DateTime(setTimeMillis).toString("dd/MM/YYYY HH:mm:ss"));
        alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, getClass()).putExtra("beginTimes", beginTimes).putExtra("endTimes", endTimes), 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, setTimeMillis, alarmIntent);
    }
}
