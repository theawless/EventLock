package com.gobbledygook.theawless.eventlock.app;

import android.content.Context;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.provider.CalendarContract;
import android.util.AttributeSet;

import com.gobbledygook.theawless.eventlock.helper.Utils;

import java.util.ArrayList;
import java.util.List;


class CalendarPreference extends MultiSelectListPreference {
    public CalendarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
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
            calendarIds.add(String.valueOf((cursor.getInt(0))));
        }
        if (cursor != null) {
            cursor.close();
            updateSameCalenders(calendarNames);
            setEntries(calendarNames.toArray(new CharSequence[calendarNames.size()]));
            setEntryValues(calendarIds.toArray(new CharSequence[calendarIds.size()]));
        }
    }

    private void updateSameCalenders(List<String> calendarNames) {
        for (int i = 0; i < calendarNames.size(); ++i) {
            List<Integer> indices = Utils.indexOfAll(calendarNames.get(i), calendarNames);
            if (indices.size() > 1) {
                for (int j = 0; j < indices.size(); ++j) {
                    calendarNames.set(indices.get(j), calendarNames.get(indices.get(j)) + " - " + j);
                }
            }
        }
    }
}
