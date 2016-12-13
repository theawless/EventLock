package com.gobbledygook.theawless.eventlock.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gobbledygook.theawless.eventlock.receivers.alarms.CurrentEventUpdaterAlarm;

import org.joda.time.DateTime;

public class CurrentEventUpdaterService extends IntentService {
    private static final String TAG = CurrentEventUpdaterService.class.getSimpleName();

    private int eventToDisplay;
    private long currentEventUpdaterTime = Long.MAX_VALUE;

    public CurrentEventUpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        long[] beginTimes = bundle.getLongArray("beginTimes");
        long[] endTimes = bundle.getLongArray("endTimes");
        if (beginTimes == null || beginTimes.length == 0) {
            return;
        }
        decideEvents(beginTimes, endTimes);
        sendBroadcast(new Intent().setAction("CurrentEventUpdate").putExtra("currentEvent", eventToDisplay));
        new CurrentEventUpdaterAlarm().setAlarm(this, currentEventUpdaterTime, beginTimes, endTimes);
        Log.v(TAG, "event to display" + eventToDisplay);
        CurrentEventUpdaterAlarm.completeWakefulIntent(intent);
    }

    private void decideEvents(long[] beginTimes, long[] endTimes) {
        long timeNow = new DateTime().getMillis(), runningEventSmallestBeginTime = Long.MAX_VALUE,
                smallestBeginTime = Long.MAX_VALUE, smallestEndTime = Long.MAX_VALUE;
        int eventIndex, closestBeginEvent = -1, runningEvent = -1;
        for (eventIndex = 0; eventIndex < beginTimes.length; eventIndex++) {
            long beginTime = beginTimes[eventIndex];
            long endTime = endTimes[eventIndex];
            if (beginTime > timeNow && beginTime < smallestBeginTime) {
                smallestBeginTime = beginTime;
                closestBeginEvent = eventIndex;
            }
            if (endTime > timeNow && endTime < smallestEndTime) {
                smallestEndTime = endTime;
            }
            if ((beginTime < timeNow && timeNow < endTime) && beginTime < runningEventSmallestBeginTime) {
                runningEventSmallestBeginTime = beginTime;
                runningEvent = eventIndex;
            }
        }
        currentEventUpdaterTime = Math.min(smallestBeginTime, smallestEndTime);
        if (runningEvent != -1) {
            eventToDisplay = runningEvent;
        } else if (closestBeginEvent != -1) {
            eventToDisplay = closestBeginEvent;
        } else {
            eventToDisplay = eventIndex;
        }
    }
}