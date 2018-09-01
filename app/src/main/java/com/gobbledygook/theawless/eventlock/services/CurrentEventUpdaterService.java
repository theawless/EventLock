package com.gobbledygook.theawless.eventlock.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.receivers.alarms.CurrentEventUpdaterAlarm;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;

public class CurrentEventUpdaterService extends IntentService {
    private static final String TAG = CurrentEventUpdaterService.class.getSimpleName();

    private int eventToScroll = -1;
    private final ArrayList<Integer> eventsToHighlight = new ArrayList<>();
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
        if (beginTimes == null || endTimes == null) {
            return;
        }
        decideEvents(beginTimes, endTimes);
        sendBroadcast(new Intent().setAction(Constants.current_events_update).putIntegerArrayListExtra(Constants.current_events, eventsToHighlight).putExtra(Constants.scroll_event, eventToScroll));
        if (currentEventUpdaterTime != Long.MAX_VALUE) {
            new CurrentEventUpdaterAlarm().setAlarm(this, currentEventUpdaterTime + 1000, new Intent().putExtra(Constants.begin_times, beginTimes).putExtra(Constants.end_times, endTimes));
        }
        CurrentEventUpdaterAlarm.completeWakefulIntent(intent);
    }

    private void decideEvents(long[] beginTimes, long[] endTimes) {
        long nowTime = DateTime.now(DateTimeZone.getDefault()).getMillis();
        long smallestBeginTime = Long.MAX_VALUE, smallestEndTime = Long.MAX_VALUE;
        int smallestBeginTimeIndex = -1;
        for (int eventIndex = 0; eventIndex < beginTimes.length; ++eventIndex) {
            long beginTime = beginTimes[eventIndex];
            long endTime = endTimes[eventIndex];
            if (beginTime > nowTime && beginTime < smallestBeginTime) {
                smallestBeginTime = beginTime;
                smallestBeginTimeIndex = eventIndex;
            }
            if (endTime > nowTime && endTime < smallestEndTime) {
                smallestEndTime = endTime;
            }
            if (beginTime < nowTime && nowTime < endTime) {
                eventsToHighlight.add(eventIndex);
            }
        }
        if (!eventsToHighlight.isEmpty()) {
            eventToScroll = eventsToHighlight.get(eventsToHighlight.size() - 1);
        } else if (smallestBeginTimeIndex != -1) {
            eventToScroll = smallestBeginTimeIndex;
        } else {
            eventToScroll = beginTimes.length - 1;
        }
        currentEventUpdaterTime = Math.min(smallestBeginTime, smallestEndTime);
    }
}
