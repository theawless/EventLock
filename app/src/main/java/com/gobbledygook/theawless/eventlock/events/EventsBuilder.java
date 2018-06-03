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
            long beginTime = cursor.getLong(2);
            long endTime = cursor.getLong(3);
            String timeZone = cursor.getString(6);
            int allDay = cursor.getInt(4);
            events.get(Enums.EventInfo.Title.ordinal()).add(getFormattedTitle(cursor.getString(0), cursor.getString(1)));
            events.get(Enums.EventInfo.Time.ordinal()).add(getFormattedTime(beginTime, endTime, allDay, timeZone));
            events.get(Enums.EventInfo.Color.ordinal()).add(cursor.getString(5));
            times.get(Enums.TimesInfo.Begin.ordinal()).add(beginTime);
            times.get(Enums.TimesInfo.End.ordinal()).add(endTime);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private String getFormattedTitle(String title, String location) {
        if (location != null && !location.isEmpty() && eventFormatter.location) {
            return title + " " + eventFormatter.at + " " + location;
        }
        return title;
    }

    private String getFormattedTime(long beginTime, long endTime, int allDay, String timeZone) {
        beginTime = new DateTime(beginTime, DateTimeZone.forID(timeZone)).withZoneRetainFields(DateTimeZone.getDefault()).getMillis();
        endTime = new DateTime(endTime, DateTimeZone.forID(timeZone)).withZoneRetainFields(DateTimeZone.getDefault()).getMillis();
        long nowTime = DateTime.now(DateTimeZone.getDefault()).getMillis();

        if (allDay == 0) {
            String bd = String.valueOf(DateUtils.getRelativeTimeSpanString(beginTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));
            String ed = String.valueOf(DateUtils.getRelativeTimeSpanString(endTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));
            String bt = String.valueOf(DateUtils.formatDateTime(context, beginTime, DateUtils.FORMAT_SHOW_TIME));
            String et = String.valueOf(DateUtils.formatDateTime(context, endTime, DateUtils.FORMAT_SHOW_TIME));

            int numberOfDays = Days.daysBetween(new DateTime(beginTime, DateTimeZone.getDefault()), new DateTime(endTime, DateTimeZone.getDefault())).getDays();
            if (numberOfDays != 0) {
                return bd + ", " + bt + " - " + ed + ", " + et;
            } else {
                return bd + ", " + bt + " - " + et;
            }
        } else {
            // making sure that prev/next day doesn't accidentally show up
            beginTime += 1;
            endTime -= 1;

            String bd = String.valueOf(DateUtils.getRelativeTimeSpanString(beginTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));
            String ed = String.valueOf(DateUtils.getRelativeTimeSpanString(endTime, nowTime, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE));

            int numberOfDays = Days.daysBetween(new DateTime(beginTime, DateTimeZone.getDefault()), new DateTime(endTime, DateTimeZone.getDefault())).getDays();
            if (numberOfDays != 0) {
                return bd + " - " + ed;
            } else {
                return bd;
            }
        }
    }

    static class EventFormatter {
        String timeFormat;
        String tomorrow;
        String day_after_tomorrow;
        String days;
        String after;
        String all_day;
        String at;
        String separator;
        boolean location;
    }
}
