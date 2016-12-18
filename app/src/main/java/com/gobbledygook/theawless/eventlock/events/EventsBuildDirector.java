package com.gobbledygook.theawless.eventlock.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import com.gobbledygook.theawless.eventlock.helper.Constants;

import java.util.ArrayList;
import java.util.Set;

public class EventsBuildDirector {
    private static final String[] EVENT_PROJECTION = {CalendarContract.Instances.TITLE, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.DISPLAY_COLOR, CalendarContract.Instances.EVENT_TIMEZONE};
    private final SharedPreferences preferences;
    private EventsBuilder eventsBuilder;

    public EventsBuildDirector(Context context) {
        eventsBuilder = new EventsBuilder(context);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void construct() {
        Set<String> selectionArgsSet = preferences.getStringSet(Constants.selected_calendars_key, Constants.selected_calendars_default);
        if (selectionArgsSet.isEmpty()) {
            return;
        }
        String selection = buildSelection(selectionArgsSet.size());
        String[] selectionArgs = selectionArgsSet.toArray(new String[selectionArgsSet.size()]);
        eventsBuilder.setUpCursor(Integer.parseInt(preferences.getString(Constants.days_till_key, Constants.days_till_default)), EVENT_PROJECTION, selection, selectionArgs);
        eventsBuilder.build(preferences.getString(Constants.time_format_key, Constants.time_format_default));
    }

    public ArrayList<String>[] getEvents() {
        return eventsBuilder.events;
    }

    public ArrayList<Long>[] getTimes() {
        return eventsBuilder.times;
    }

    private String buildSelection(int length) {
        String selection = "( " + CalendarContract.Instances.CALENDAR_ID + " = ?";
        for (int i = 0; i < length - 1; i++) {
            selection = selection + " OR " + CalendarContract.Instances.CALENDAR_ID + " = ?";
        }
        selection = selection + " )";
        return selection;
    }
}
