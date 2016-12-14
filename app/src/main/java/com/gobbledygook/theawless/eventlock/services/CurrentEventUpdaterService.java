package com.gobbledygook.theawless.eventlock.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.receivers.alarms.CurrentEventUpdaterAlarm;

import org.joda.time.DateTime;

public class CurrentEventUpdaterService extends IntentService {
    private static final String TAG = CurrentEventUpdaterService.class.getSimpleName();

    private int eventToDisplay;
    private int currentEventUpdaterIndex = -1;
    private long currentEventUpdaterTime = Long.MAX_VALUE;

    public CurrentEventUpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        long[] beginTimes = bundle.getLongArray(Constants.begin_times);
        long[] endTimes = bundle.getLongArray(Constants.end_times);
        if (beginTimes == null || beginTimes.length == 0) {
            return;
        }
        decideEvents(beginTimes, endTimes);
        sendBroadcast(new Intent().setAction(Constants.current_event_update).putExtra(Constants.current_event, eventToDisplay));
        Log.v(TAG, "Event to display" + eventToDisplay);
        if (currentEventUpdaterIndex != -1 && endTimes[currentEventUpdaterIndex] != Long.MAX_VALUE) {
            new CurrentEventUpdaterAlarm().setAlarm(this, currentEventUpdaterTime, beginTimes, endTimes);
        } else {
            Log.v(TAG, "Prevented repeat alarm!");
        }
        CurrentEventUpdaterAlarm.completeWakefulIntent(intent);
    }

    private void decideEvents(long[] beginTimes, long[] endTimes) {
        long timeNow = new DateTime().getMillis(), runningEventLargestBeginTime = -1,
                smallestBeginTime = Long.MAX_VALUE, smallestEndTime = Long.MAX_VALUE;
        int eventIndex, closestBeginEvent = -1, runningEvent = -1,
                smallestBeginTimeIndex = -1, smallestEndTimeIndex = -1;
        for (eventIndex = 0; eventIndex < beginTimes.length; eventIndex++) {
            long beginTime = beginTimes[eventIndex];
            long endTime = endTimes[eventIndex];
            if (beginTime > timeNow && beginTime < smallestBeginTime) {
                smallestBeginTime = beginTime;
                closestBeginEvent = eventIndex;
                smallestBeginTimeIndex = eventIndex;
            }
            if (endTime > timeNow && endTime < smallestEndTime) {
                smallestEndTime = endTime;
                smallestEndTimeIndex = eventIndex;
            }
            if ((beginTime < timeNow && timeNow < endTime) && beginTime > runningEventLargestBeginTime) {
                runningEventLargestBeginTime = beginTime;
                runningEvent = eventIndex;
            }
        }
        if (smallestBeginTime < smallestEndTime) {
            currentEventUpdaterTime = smallestBeginTime;
            currentEventUpdaterIndex = smallestBeginTimeIndex;
        } else {
            currentEventUpdaterTime = smallestEndTime;
            currentEventUpdaterIndex = smallestEndTimeIndex;
        }
        if (runningEvent != -1) {
            eventToDisplay = runningEvent;
        } else if (closestBeginEvent != -1) {
            eventToDisplay = closestBeginEvent;
        } else {
            eventToDisplay = eventIndex;
        }
    }
}