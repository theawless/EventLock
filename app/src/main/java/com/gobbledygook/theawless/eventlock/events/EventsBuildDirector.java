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
    private final EventsBuilder eventsBuilder;

    public EventsBuildDirector(Context context) {
        eventsBuilder = new EventsBuilder(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void construct() {
        Set<String> selectionArgsSet = preferences.getStringSet(Constants.selected_calendars_key, Constants.selected_calendars_default);
        if (selectionArgsSet.isEmpty()) {
            return;
        }
        String selection = buildSelection(selectionArgsSet.size());
        String[] selectionArgs = selectionArgsSet.toArray(new String[0]);
        eventsBuilder.setEventFormatter(buildTimeFormatter());
        eventsBuilder.setUpCursor(Integer.parseInt(preferences.getString(Constants.days_till_key, Constants.days_till_default)), EVENT_PROJECTION, selection, selectionArgs);
        eventsBuilder.build();
    }

    public ArrayList<ArrayList<String>> getEvents() {
        return eventsBuilder.events;
    }

    public ArrayList<ArrayList<Long>> getTimes() {
        return eventsBuilder.times;
    }

    private String buildSelection(int length) {
        StringBuilder selection = new StringBuilder("( " + CalendarContract.Instances.CALENDAR_ID + " = ?");
        for (int i = 0; i < length - 1; i++) {
            selection.append(" OR " + CalendarContract.Instances.CALENDAR_ID + " = ?");
        }
        selection.append(" )");
        return selection.toString();
    }

    private EventsBuilder.EventFormatter buildTimeFormatter() {
        EventsBuilder.EventFormatter eventFormatter = new EventsBuilder.EventFormatter();
        eventFormatter.timeSeparator = preferences.getString(Constants.time_separator_key, Constants.time_separator_default);
        eventFormatter.rangeSeparator = preferences.getString(Constants.range_separator_key, Constants.range_separator_default);
        eventFormatter.locationSeparator = preferences.getString(Constants.location_separator_key, Constants.location_separator_default);
        String location = preferences.getString(Constants.location_key, Constants.location_default);
        switch (location) {
            case "title":
                eventFormatter.location = true;
                eventFormatter.locationWithTitle = true;
                break;
            case "time":
                eventFormatter.location = true;
                eventFormatter.locationWithTitle = false;
                break;
            case "off":
                eventFormatter.location = false;
                break;
        }
        return eventFormatter;
    }
}
