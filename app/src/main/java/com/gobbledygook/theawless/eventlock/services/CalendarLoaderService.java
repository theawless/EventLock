package com.gobbledygook.theawless.eventlock.services;


import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.gobbledygook.theawless.eventlock.events.EventsBuildDirector;
import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.helper.Enums;
import com.gobbledygook.theawless.eventlock.receivers.alarms.CalendarLoaderAlarm;

import org.joda.time.DateTime;

import java.util.ArrayList;

//sets CalendarLoaderAlarm to receive at midnights, or when refresh is called
public class CalendarLoaderService extends IntentService {
    private static final String TAG = CalendarLoaderService.class.getSimpleName();

    public CalendarLoaderService() {
        super(TAG);
    }

    private static long[] longArrayListToPrimitive(ArrayList<Long> longArrayList) {
        if (longArrayList == null || longArrayList.size() == 0) {
            return null;
        }
        long primitiveLongArray[] = new long[longArrayList.size()];
        for (int i = 0; i < longArrayList.size(); i++) {
            primitiveLongArray[i] = longArrayList.get(i);
        }
        return primitiveLongArray;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)) {
            EventsBuildDirector eventsBuildDirector = new EventsBuildDirector(this);
            eventsBuildDirector.construct();
            ArrayList<String>[] events = eventsBuildDirector.getEvents();
            sendBroadcast(new Intent()
                    .setAction(Constants.events_update)
                    .putStringArrayListExtra(Constants.formatted_titles, events[Enums.EventInfo.Title.ordinal()])
                    .putStringArrayListExtra(Constants.formatted_times, events[Enums.EventInfo.Time.ordinal()])
                    .putStringArrayListExtra(Constants.colors, events[Enums.EventInfo.Color.ordinal()])
            );
            ArrayList<Long>[] times = eventsBuildDirector.getTimes();
            startCurrentEventUpdater(times[Enums.TimesInfo.Begin.ordinal()], times[Enums.TimesInfo.End.ordinal()]);
        }
        setAlarm();
        CalendarLoaderAlarm.completeWakefulIntent(intent);
    }

    private void setAlarm() {
        new CalendarLoaderAlarm().setAlarm(this, new DateTime().plusDays(1).withTimeAtStartOfDay().plusSeconds(1).getMillis());
    }

    private void startCurrentEventUpdater(ArrayList<Long> beginTimes, ArrayList<Long> endTimes) {
        startService(new Intent(this, CurrentEventUpdaterService.class)
                .putExtra(Constants.begin_times, longArrayListToPrimitive(beginTimes))
                .putExtra(Constants.end_times, longArrayListToPrimitive(endTimes))
        );
    }
}