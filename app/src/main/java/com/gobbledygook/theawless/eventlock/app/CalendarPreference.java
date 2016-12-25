package com.gobbledygook.theawless.eventlock.app;

import android.content.Context;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.provider.CalendarContract;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;


class CalendarPreference extends MultiSelectListPreference {

    public CalendarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //TODO differentiate between various types of calendars

    @SuppressWarnings("MissingPermission")
    void getCalendarIds() {
        Cursor cursor = getContext().getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME},
                null, null, null
        );
        List<String> calendarNames = new ArrayList<>();
        List<String> calendarIds = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            calendarNames.add(cursor.getString(1));
            calendarIds.add(String.valueOf((cursor.getInt(0))));
        }
        if (cursor != null) {
            cursor.close();
            setEntries(calendarNames.toArray(new CharSequence[calendarNames.size()]));
            setEntryValues(calendarIds.toArray(new CharSequence[calendarNames.size()]));
        }
    }
}