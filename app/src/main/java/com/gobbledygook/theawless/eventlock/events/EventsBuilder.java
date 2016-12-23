package com.gobbledygook.theawless.eventlock.events;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.helper.Enums;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class EventsBuilder {
    private final Context context;
    ArrayList<String>[] events = (ArrayList<String>[]) new ArrayList[Enums.EventInfo.values().length];
    ArrayList<Long>[] times = (ArrayList<Long>[]) new ArrayList[Enums.TimesInfo.values().length];
    private Cursor cursor;

    EventsBuilder(Context context) {
        this.context = context;
        events[Enums.EventInfo.Title.ordinal()] = new ArrayList<>();
        events[Enums.EventInfo.Time.ordinal()] = new ArrayList<>();
        events[Enums.EventInfo.Color.ordinal()] = new ArrayList<>();
        times[Enums.TimesInfo.Begin.ordinal()] = new ArrayList<>();
        times[Enums.TimesInfo.End.ordinal()] = new ArrayList<>();
    }

    void setUpCursor(int daysTill, String[] eventProjection, String selection, String[] selectionArgs) {
        DateTime todayStart = new DateTime().withTimeAtStartOfDay();
        DateTime daysTillEnd = todayStart.plusDays(daysTill).withTimeAtStartOfDay();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, todayStart.getMillis());
        ContentUris.appendId(eventsUriBuilder, daysTillEnd.getMillis());
        cursor = context.getContentResolver().query(eventsUriBuilder.build(), eventProjection, selection, selectionArgs, CalendarContract.Instances.BEGIN + " ASC");
    }

    void build(String timeFormat) {
        while (cursor != null && cursor.moveToNext()) {
            int allDay = cursor.getInt(4);
            long beginTime, endTime;
            if (allDay == 1) {
                beginTime = new DateTime(cursor.getLong(2), DateTimeZone.forID(cursor.getString(6))).withZone(DateTimeZone.getDefault()).withTimeAtStartOfDay().getMillis();
                //to prevent multiple alarms at midnight. Even if current event says alarm at midnight, we anyway have loader alarm
                endTime = Long.MAX_VALUE;
            } else {
                beginTime = new DateTime(cursor.getLong(2), DateTimeZone.forID(cursor.getString(6))).withZone(DateTimeZone.getDefault()).getMillis();
                endTime = new DateTime(cursor.getLong(3), DateTimeZone.forID(cursor.getString(6))).withZone(DateTimeZone.getDefault()).getMillis();
            }
            int dayDiff = Days.daysBetween(new DateTime().withTimeAtStartOfDay().toLocalDate(), new DateTime(beginTime).toLocalDate()).getDays();
            if (dayDiff >= 0) {
                events[Enums.EventInfo.Title.ordinal()].add(getFormattedTitle(cursor.getString(0), cursor.getString(1)));
                events[Enums.EventInfo.Time.ordinal()].add(getFormattedTime(beginTime, endTime, allDay, dayDiff, timeFormat));
                events[Enums.EventInfo.Color.ordinal()].add(cursor.getString(5));
                times[Enums.TimesInfo.Begin.ordinal()].add(beginTime);
                times[Enums.TimesInfo.End.ordinal()].add(endTime);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private String getFormattedTitle(String title, String location) {
        if (location != null && !location.isEmpty()) {
            return title + " " + context.getString(R.string.at) + " " + location;
        }
        return title;
    }

    private String getFormattedTime(long beginTime, long endTime, int allDay, int dayDiff, String timeFormat) {
        DateFormat formatter;
        switch (timeFormat) {
            case "12hr": {
                formatter = new SimpleDateFormat("hh:mm a");
                break;
            }
            case "24hr": {
                formatter = new SimpleDateFormat("HH:mm");
                break;
            }
            default: {
                formatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
            }
        }
        String time = formatter.format(beginTime) + " - " + formatter.format(endTime);
        if (allDay == 1) {
            time = context.getString(R.string.all_day);
        }
        switch (dayDiff) {
            case 0: {
                break;
            }
            case 1: {
                time += context.getString(R.string.comma) + " " + context.getString(R.string.tomorrow);
                break;
            }
            case 2: {
                time += context.getString(R.string.comma) + " " + context.getString(R.string.day_after_tomorrow);
                break;
            }
            default: {
                time += context.getString(R.string.comma) + " " + context.getString(R.string.after) + " " + (dayDiff - 1) + " " + context.getString(R.string.days);
            }
        }
        return time;
    }
}