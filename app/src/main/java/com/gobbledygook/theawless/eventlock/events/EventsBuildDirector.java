package com.gobbledygook.theawless.eventlock.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.helper.Constants;

import java.util.ArrayList;
import java.util.Set;

public class EventsBuildDirector {
    private static final String[] EVENT_PROJECTION = {CalendarContract.Instances.TITLE, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.DISPLAY_COLOR, CalendarContract.Instances.EVENT_TIMEZONE};
    private final SharedPreferences preferences;
    private final EventsBuilder eventsBuilder;
    private final Context context;

    public EventsBuildDirector(Context context) {
        this.context = context;
        eventsBuilder = new EventsBuilder(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void construct() {
        Set<String> selectionArgsSet = preferences.getStringSet(Constants.selected_calendars_key, Constants.selected_calendars_default);
        if (selectionArgsSet.isEmpty()) {
            return;
        }
        String selection = buildSelection(selectionArgsSet.size());
        String[] selectionArgs = selectionArgsSet.toArray(new String[selectionArgsSet.size()]);
        eventsBuilder.setEventFormatter(buildTimeFormatter());
        eventsBuilder.setUpCursor(Integer.parseInt(preferences.getString(Constants.days_till_key, Constants.days_till_default)), EVENT_PROJECTION, selection, selectionArgs);
        eventsBuilder.build();
    }

    public ArrayList<String>[] getEvents() {
        return eventsBuilder.events;
    }

    public ArrayList<Long>[] getTimes() {
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
        eventFormatter.timeFormat = preferences.getString(Constants.time_format_key, Constants.time_format_default);
        eventFormatter.tomorrow = preferences.getString(Constants.tomorrow_key, context.getString(R.string.tomorrow_default));
        eventFormatter.day_after_tomorrow = preferences.getString(Constants.day_after_tomorrow_key, context.getString(R.string.day_after_tomorrow_default));
        eventFormatter.days = preferences.getString(Constants.days_key, context.getString(R.string.days_default));
        eventFormatter.after = preferences.getString(Constants.after_key, context.getString(R.string.after_default));
        eventFormatter.all_day = preferences.getString(Constants.all_day_key, context.getString(R.string.all_day_default));
        eventFormatter.at = preferences.getString(Constants.at_key, context.getString(R.string.at_default));
        eventFormatter.separator = preferences.getString(Constants.separator_key, context.getString(R.string.separator_default));
        eventFormatter.location = preferences.getBoolean(Constants.location_key, Boolean.parseBoolean(Constants.location_default));
        return eventFormatter;
    }
}
