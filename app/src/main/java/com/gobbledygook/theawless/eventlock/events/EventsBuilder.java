package com.gobbledygook.theawless.eventlock.events;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;

import com.gobbledygook.theawless.eventlock.helper.Enums;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Arrays;

class EventsBuilder {
    final ArrayList<ArrayList<String>> events = new ArrayList<>(Arrays.asList(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>()));
    final ArrayList<ArrayList<Long>> times = new ArrayList<>(Arrays.asList(new ArrayList<Long>(), new ArrayList<Long>()));
    private final Context context;
    private Cursor cursor;
    private EventFormatter eventFormatter;

    EventsBuilder(Context context) {
        this.context = context;
    }

    void setEventFormatter(EventFormatter eventFormatter) {
        this.eventFormatter = eventFormatter;
    }

    void setUpCursor(int daysTill, String[] eventProjection, String selection, String[] selectionArgs) {
        DateTime todayStart = new DateTime().withTimeAtStartOfDay();
        DateTime daysTillEnd = todayStart.plusDays(daysTill).withTimeAtStartOfDay();
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, todayStart.getMillis());
        ContentUris.appendId(eventsUriBuilder, daysTillEnd.getMillis());
        cursor = context.getContentResolver().query(eventsUriBuilder.build(), eventProjection, selection, selectionArgs, CalendarContract.Instances.BEGIN + " ASC");
    }

    void build() {
        while (cursor != null && cursor.moveToNext()) {
            String timeZone = cursor.getString(6);
            long beginTime = new DateTime(cursor.getLong(2), DateTimeZone.forID(timeZone)).withZoneRetainFields(DateTimeZone.getDefault()).getMillis();
            long endTime = new DateTime(cursor.getLong(3), DateTimeZone.forID(timeZone)).withZoneRetainFields(DateTimeZone.getDefault()).getMillis();
            int allDay = cursor.getInt(4);
            String title = cursor.getString(0);
            String location = cursor.getString(1);
            String time = getFormattedTime(beginTime, endTime, allDay);
            String color = cursor.getString(5);
            if (location != null && !location.isEmpty() && eventFormatter.location) {
                if (eventFormatter.locationWithTitle) {
                    title += eventFormatter.locationSeparator + location;
                } else {
                    time += eventFormatter.locationSeparator + location;
                }
            }

            if (endTime >= DateTime.now(DateTimeZone.getDefault()).getMillis()) {
                events.get(Enums.EventInfo.Title.ordinal()).add(title);
                events.get(Enums.EventInfo.Time.ordinal()).add(time);
                events.get(Enums.EventInfo.Color.ordinal()).add(color);
                times.get(Enums.TimesInfo.Begin.ordinal()).add(beginTime);
                times.get(Enums.TimesInfo.End.ordinal()).add(endTime);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private String getFormattedTime(long beginTime, long endTime, int allDay) {
        long nowTime = DateTime.now(DateTimeZone.getDefault()).getMillis();
        if (allDay == 0) {
            String bd = String.valueOf(DateUtils.getRelativeTimeSpanString(beginTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));
            String ed = String.valueOf(DateUtils.getRelativeTimeSpanString(endTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));
            String bt = String.valueOf(DateUtils.formatDateTime(context, beginTime, DateUtils.FORMAT_SHOW_TIME));
            String et = String.valueOf(DateUtils.formatDateTime(context, endTime, DateUtils.FORMAT_SHOW_TIME));

            int numberOfDays = Days.daysBetween(new DateTime(beginTime, DateTimeZone.getDefault()), new DateTime(endTime, DateTimeZone.getDefault())).getDays();
            if (numberOfDays != 0) {
                return bd + eventFormatter.timeSeparator + bt + eventFormatter.rangeSeparator + ed + eventFormatter.timeSeparator + et;
            } else {
                return bd + eventFormatter.timeSeparator + bt + eventFormatter.rangeSeparator + et;
            }
        } else {
            // making sure that prev/next day doesn't accidentally show up
            beginTime += 1;
            endTime -= 1;

            String bd = String.valueOf(DateUtils.getRelativeTimeSpanString(beginTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));
            String ed = String.valueOf(DateUtils.getRelativeTimeSpanString(endTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));

            int numberOfDays = Days.daysBetween(new DateTime(beginTime, DateTimeZone.getDefault()), new DateTime(endTime, DateTimeZone.getDefault())).getDays();
            if (numberOfDays != 0) {
                return bd + eventFormatter.rangeSeparator + ed;
            } else {
                return bd;
            }
        }
    }

    static class EventFormatter {
        String timeSeparator;
        String rangeSeparator;
        String locationSeparator;
        boolean location;
        boolean locationWithTitle;
    }
}
