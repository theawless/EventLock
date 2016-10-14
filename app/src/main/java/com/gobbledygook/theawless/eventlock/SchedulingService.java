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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SchedulingService extends IntentService {
    private static final String TAG = "SchedulingService";
    private static final String[] EVENT_PROJECTION = {CalendarContract.Instances.TITLE, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY};
    private AlarmReceiver alarmReceiver = new AlarmReceiver();
    private Context context;
    private SharedPreferences preferences;

    public SchedulingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent");
        handleIntent(this);
        AlarmReceiver.completeWakefulIntent(intent);
    }

    protected void handleIntent(Context context) {
        Log.v(TAG, "handleIntent");
        preferences = context.getSharedPreferences(context.getString(R.string.preferences), MODE_PRIVATE);
        this.context = context;
        if (!checkPermissions()) {
            return;
        }
        String[] default_selected_calendars_array = context.getResources().getStringArray(R.array.selected_calendars_default);
        Set<String> selected_calendars_set_default = new HashSet<>();
        Collections.addAll(selected_calendars_set_default, default_selected_calendars_array);
        Set<String> selectionArgsSet = preferences.getStringSet(context.getString(R.string.selected_calendars_key), selected_calendars_set_default);
        if (selectionArgsSet.isEmpty()) {
            //no calendars set, quit the service
            setTitleAndTimePrefs("", "");
            stopSelf();
        }
        queryCalendarEvents(buildSelection(selectionArgsSet.size()), selectionArgsSet.toArray(new String[selectionArgsSet.size()]));
    }

    private void queryCalendarEvents(String selection, String[] selectionArgs) {
        Calendar calendar = Calendar.getInstance();
        long from_time = calendar.getTimeInMillis();
        if (!preferences.getBoolean(context.getString(R.string.from_key), Boolean.parseBoolean(context.getString(R.string.from_default)))) {
            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            from_time = calendar.getTimeInMillis();
        }
        int to_key_value = Integer.parseInt(preferences.getString(context.getString(R.string.to_key), context.getString(R.string.to_default)));
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        long tonight_time = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, to_key_value);
        long to_time = calendar.getTimeInMillis();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, from_time);
        ContentUris.appendId(eventsUriBuilder, to_time);
        String finalTime = "", finalTitle = "";
        Cursor cursor = context.getContentResolver().query(eventsUriBuilder.build(), EVENT_PROJECTION, selection, selectionArgs, CalendarContract.Instances.BEGIN + " ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String eventTitle = cursor.getString(0);
            String eventLocation = cursor.getString(1);
            long beginTime = cursor.getLong(2);
            long endTime = cursor.getLong(3);
            int allDay = cursor.getInt(4);
            cursor.close();
            finalTitle = eventTitle;
            if (!TextUtils.isEmpty(eventLocation)) {
                finalTitle = finalTitle + " " + context.getString(R.string.at) + " " + eventLocation;
            }
            DateFormat formatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
            finalTime = formatter.format(beginTime) + " - " + formatter.format(endTime);
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
            //event ends at endTime, let's check back at endTime + 2 second
            if (endTime < tonight_time) {
                //compulsary check at each midnight
                tonight_time = endTime;
            }
        } else if (cursor != null) {
            cursor.close();
        }
        //no events in the upcoming days, let's check back at midnight + 2 second
        setTitleAndTimePrefs(finalTitle, finalTime);
        alarmReceiver.setAlarm(context, tonight_time + 1000 * 2);
    }

    private void setTitleAndTimePrefs(String title, String time) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.event_title_key), title);
        editor.putString(context.getString(R.string.event_time_key), time);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "No READ_CALENDAR permission");
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(context.getString(R.string.event_title_key));
            editor.remove(context.getString(R.string.event_time_key));
            editor.apply();
            return false;
        }
        return true;
    }
}