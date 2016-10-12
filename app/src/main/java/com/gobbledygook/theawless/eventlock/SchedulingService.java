package com.gobbledygook.theawless.eventlock;


import android.Manifest;
import android.app.IntentService;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SchedulingService extends IntentService {
    private static final String TAG = "SchedulingService";
    private static final String[] EVENT_PROJECTION = {CalendarContract.Instances.TITLE, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY};
    private static final String SELECTION = CalendarContract.Instances.CALENDAR_ID + " = ?";
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
        SharedPreferences preferences = context.getSharedPreferences(PreferenceConsts.preferences, MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        long from_time = calendar.getTimeInMillis();
        if (!preferences.getBoolean(PreferenceConsts.from_key, Boolean.parseBoolean(PreferenceConsts.from_default))) {
            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            from_time = calendar.getTimeInMillis();
        }
        int to_key_value = Integer.parseInt(preferences.getString(PreferenceConsts.to_key, PreferenceConsts.to_default));
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.add(Calendar.DATE, to_key_value);
        long to_time = calendar.getTimeInMillis();
        SharedPreferences.Editor editor = preferences.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "No READ_CALENDAR permission");
            editor.remove(PreferenceConsts.event_title_key);
            editor.remove(PreferenceConsts.event_time_key);
            return;
        }
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, from_time);
        ContentUris.appendId(eventsUriBuilder, to_time);
        String[] selectionArgs = new String[]{preferences.getString(PreferenceConsts.selected_calendars_key, PreferenceConsts.selected_calendars_default)};
        Cursor cursor = context.getContentResolver().query(eventsUriBuilder.build(), EVENT_PROJECTION, SELECTION, selectionArgs, CalendarContract.Instances.BEGIN + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String eventTitle = cursor.getString(0);
            String eventLocation = cursor.getString(1);
            long beginTime = cursor.getLong(2);
            long endTime = cursor.getLong(3);
            int allDay = cursor.getInt(4);
            cursor.close();
            String finalTitle = eventTitle;
            if (!TextUtils.isEmpty(eventLocation)) {
                finalTitle = finalTitle + " " + context.getString(R.string.at) + " " + eventLocation;
            }
            DateFormat formatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
            String finalTime = formatter.format(beginTime) + " - " + formatter.format(endTime);
            if (allDay == 1) {
                finalTime = context.getString(R.string.all_day);
            }
            calendar.add(Calendar.DATE, -to_key_value);
            int today = calendar.get(Calendar.DATE);
            calendar.setTimeInMillis(beginTime);
            int other_day = calendar.get(Calendar.DATE);
            int day_diff = other_day - today;
            switch (day_diff) {
                case 0: {
                    break;
                }
                case 1: {
                    finalTime = finalTime + ", " + context.getString(R.string.tomorrow);
                    break;
                }
                case 2: {
                    finalTime = finalTime + ", " + context.getString(R.string.day_after_tomorrow);
                    break;
                }
                default: {
                    finalTime = finalTime + ", " + context.getString(R.string.after) + " " + (day_diff - 1) + " " + context.getString(R.string.days);
                }
            }
            editor.putString(PreferenceConsts.event_title_key, finalTitle);
            editor.putString(PreferenceConsts.event_time_key, finalTime);
            editor.apply();
            //event ends at endTime, let's check back at endTime + 2 second
            alarmReceiver.setAlarm(context, endTime + 1000 * 2);
            return;
        }
        if (cursor != null) {
            cursor.close();
        }
        editor.putString(PreferenceConsts.event_title_key, "");
        editor.putString(PreferenceConsts.event_time_key, "");
        editor.apply();
        //no events in the upcoming days, let's check back at toTime + 2 second
        alarmReceiver.setAlarm(context, to_time + 1000 * 2);
    }
}