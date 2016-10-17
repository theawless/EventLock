package com.gobbledygook.theawless.eventlock.background;


import android.Manifest;
import android.app.IntentService;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.helper.Event;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//sets CalendarLoaderAlarm to receive at midnights
public class CalendarLoaderService extends IntentService {
    private static final String TAG = "CalendarLoaderService";
    private static final String[] EVENT_PROJECTION = {CalendarContract.Instances.TITLE, CalendarContract.Instances.DESCRIPTION, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.CALENDAR_COLOR};
    private CalendarLoaderAlarm calendarLoaderAlarm = new CalendarLoaderAlarm();
    private CurrentEventUpdaterAlarm currentEventUpdaterAlarm = new CurrentEventUpdaterAlarm();
    private SharedPreferences preferences;
    private ArrayList<Event> events = new ArrayList<>();
    private int eventToDisplay;
    private long currentEventUpdaterTime = Long.MAX_VALUE;
    private long calendarLoaderAlarmTime;

    public CalendarLoaderService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent");
        handleIntent();
        //if got called by alarm, complete intent else blah blah blah
        CalendarLoaderAlarm.completeWakefulIntent(intent);
    }

    private void handleIntent() {
        Log.v(TAG, "handleIntent");
        preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        if (!checkPermissions()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(getString(R.string.event_titles_key));
            editor.remove(getString(R.string.event_times_key));
            editor.apply();
            return;
        }
        String[] default_selected_calendars_array = getResources().getStringArray(R.array.selected_calendars_default);
        Set<String> selected_calendars_set_default = new HashSet<>();
        Collections.addAll(selected_calendars_set_default, default_selected_calendars_array);
        Set<String> selectionArgsSet = preferences.getStringSet(getString(R.string.selected_calendars_key), selected_calendars_set_default);
        if (selectionArgsSet.isEmpty()) {
            //no calendars set, quit the service
            setTitlesAndTimesInPrefs();
            stopSelf();
        }
        String selection = buildSelection(selectionArgsSet.size());
        String[] selectionArgs = selectionArgsSet.toArray(new String[selectionArgsSet.size()]);
        queryEvent(selection, selectionArgs);
        setTitlesAndTimesInPrefs();
        //set alarms after two seconds
        calendarLoaderAlarm.setAlarm(this, calendarLoaderAlarmTime + 1000 * 2);
        //if updatertime >= calendarloader, then don't set alarm, because loader will call itself again and set everything again
        if (currentEventUpdaterTime != Long.MAX_VALUE && currentEventUpdaterTime >= calendarLoaderAlarmTime) {
            //call eventupdater which will set the right current event on lockscreen
            //by updating display_event, then it proceeds to make an alarm, and gets called again. LOOP.
            currentEventUpdaterAlarm.setAlarm(this, currentEventUpdaterTime + 1000 * 2);
        }
    }

    private void queryEvent(String selection, String[] selectionArgs) {
        Calendar calendar = Calendar.getInstance();
        long timeNow = calendar.getTimeInMillis();
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long todayStart = calendar.getTimeInMillis();
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendarLoaderAlarmTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, Integer.parseInt(preferences.getString(getString(R.string.days_till_key), getString(R.string.days_till_default))));
        long daysTillEnd = calendar.getTimeInMillis();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, todayStart);
        ContentUris.appendId(eventsUriBuilder, daysTillEnd);
        Cursor cursor = getContentResolver().query(eventsUriBuilder.build(), EVENT_PROJECTION, selection, selectionArgs, CalendarContract.Instances.BEGIN + " ASC");
        int closestEventTillNow = -1, currentEvent = -1, acceptableEventIndex = 0;
        long closestEventTillNowBeginTime = Integer.MAX_VALUE, currentEventBeginTime = Integer.MIN_VALUE;
        while (cursor != null && cursor.moveToNext()) {
            Event event = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4), cursor.getInt(5), cursor.getString(6));
            //sometimes the cursor has weird values, trying my best to handle them
            if (event.day_diff >= 0) {
                if (event.endTime < currentEventUpdaterTime && event.endTime > timeNow) {
                    currentEventUpdaterTime = event.endTime;
                }
                if (event.beginTime > timeNow && event.beginTime < closestEventTillNowBeginTime) {
                    closestEventTillNowBeginTime = event.beginTime;
                    closestEventTillNow = acceptableEventIndex;
                }
                if (event.beginTime < timeNow && timeNow < event.endTime && event.beginTime > currentEventBeginTime) {
                    currentEventBeginTime = event.beginTime;
                    currentEvent = acceptableEventIndex;
                }
                acceptableEventIndex++;
                events.add(event);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        //adding empty event in the end, this is tell the lockscreenhook when no events are present
        events.add(new Event("", "", "", -1, -1, -1, ""));
        if (currentEvent != -1) {
            eventToDisplay = currentEvent;
        } else if (currentEvent == -1 && closestEventTillNow != -1) {
            eventToDisplay = closestEventTillNow;
        } else {
            eventToDisplay = acceptableEventIndex;
        }
    }

    private void setTitlesAndTimesInPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        //convert to JSON
        //because prefs.putStringSet wouldn't work for equal titles and times
        JSONArray titles = new JSONArray(), times = new JSONArray(), longEndTimes = new JSONArray();
        for (Event event : events) {
            titles.put(event.getFormattedTitle(this));
            times.put(event.getFormattedTime(this));
            longEndTimes.put(event.endTime);
        }
        editor.putString(getString(R.string.event_titles_key), titles.toString());
        editor.putString(getString(R.string.event_times_key), times.toString());
        editor.putString(getString(R.string.event_long_end_times_key), longEndTimes.toString());
        editor.putInt(getString(R.string.event_to_display_key), eventToDisplay);
        editor.apply();
    }

    private String buildSelection(int length) {
        String selection = "( " + CalendarContract.Instances.CALENDAR_ID + " = ?";
        for (int i = 0; i < length - 1; i++) {
            selection = selection + " OR " + CalendarContract.Instances.CALENDAR_ID + " = ?";
        }
        selection = selection + " )";
        return selection;
    }

    private boolean checkPermissions() {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED);
    }
}

