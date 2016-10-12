package com.gobbledygook.theawless.eventlock;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class SchedulingService extends IntentService {
    private static final String TAG = "SchedulingService";
    private AlarmReceiver alarmReceiver = new AlarmReceiver();

    public SchedulingService() {
        super(TAG);
        Log.v(TAG, "Constructing");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent");
        handleIntent(this);
        AlarmReceiver.completeWakefulIntent(intent);
    }

    protected void handleIntent(Context context) {
        Log.v(TAG, "handleIntent");
        callAlarm(context);
    }

    private void callAlarm(Context context) {
        Log.v(TAG, "callAlarm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        alarmReceiver.setAlarm(context, calendar.getTimeInMillis());
    }
}
