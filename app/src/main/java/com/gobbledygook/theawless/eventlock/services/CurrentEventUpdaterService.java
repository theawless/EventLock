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
        if (bundle == null) {
            return;
        }
        long[] beginTimes = bundle.getLongArray(Constants.begin_times);
        long[] endTimes = bundle.getLongArray(Constants.end_times);
        if (beginTimes == null || beginTimes.length == 0) {
            return;
        }
        decideEvents(beginTimes, endTimes);
        sendBroadcast(new Intent().setAction(Constants.current_event_update).putExtra(Constants.current_event, eventToDisplay));
        //currentEventUpdaterIndex value ranges from 0 to beginTimes.length
        //for all day times, we had set end time =  max value. hence we know it is all day event, can ignore this alarm because loader will run at midnight anyway
        if (currentEventUpdaterIndex != -1 && endTimes[currentEventUpdaterIndex] != Long.MAX_VALUE) {
            //add a padding of 1 second
            new CurrentEventUpdaterAlarm().setAlarm(this, currentEventUpdaterTime + 1000, new Intent().putExtra("beginTimes", beginTimes).putExtra("endTimes", endTimes));
        } else {
            Log.v(TAG, "Prevented repeat alarm!");
        }
        CurrentEventUpdaterAlarm.completeWakefulIntent(intent);
    }

    //if there is a running event, show it first
    //if there are multiple running events, show the last one
    //else
    //if there is a closest event, which is the smallest end/begin time, show it
    //else
    //show last index, which the gismo will show as no more events
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
            Log.v(TAG, eventToDisplay + " runningEvent");
        } else if (closestBeginEvent != -1) {
            eventToDisplay = closestBeginEvent;
            Log.v(TAG, eventToDisplay + " closestEvent");
        } else {
            eventToDisplay = eventIndex;
            Log.v(TAG, eventToDisplay + " noEvent");
        }
    }
}
