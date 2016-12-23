package com.gobbledygook.theawless.eventlock.app;

import android.content.Context;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


class CalendarPreference extends MultiSelectListPreference {

    public CalendarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        return super.getView(convertView, parent);
    }

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
            calendarIds.add(Integer.valueOf(cursor.getInt(0)).toString());
        }
        if (cursor != null) {
            cursor.close();
            setEntries(calendarNames.toArray(new CharSequence[calendarNames.size()]));
            setEntryValues(calendarIds.toArray(new CharSequence[calendarNames.size()]));
        }
    }
}