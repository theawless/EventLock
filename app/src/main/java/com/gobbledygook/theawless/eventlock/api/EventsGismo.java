package com.gobbledygook.theawless.eventlock.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class EventsGismo {
    private RecyclerView recyclerView = null;
    private RecyclerView.LayoutManager layoutManager;
    private EventsAdapter eventsAdapter;
    private Context appContext;
    private Context gismoContext;
    private SharedPreferences preferences;

    public EventsGismo(Context appContext, Context gismoContext) {
        this.appContext = appContext;
        this.gismoContext = gismoContext;
        eventsAdapter = new EventsAdapter(appContext, PreferenceManager.getDefaultSharedPreferences(appContext));
    }

    public EventsGismo(Context context) {
        this(context, context);
    }

    public RecyclerView getRecyclerView() {
        if (recyclerView == null) {
            layoutManager = new LinearLayoutManager(gismoContext, LinearLayoutManager.HORIZONTAL, false);
            recyclerView = new RecyclerView(gismoContext);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(eventsAdapter);
            new LinearSnapHelper().attachToRecyclerView(recyclerView);
        }
        return recyclerView;
    }

    public void scrollToCurrentEvent() {
        layoutManager.scrollToPosition(eventsAdapter.currentEventIndex);
    }

    private void setCurrentEvent(int eventToDisplay) {
        eventsAdapter.currentEventIndex = eventToDisplay;
        if (layoutManager != null) {
            layoutManager.scrollToPosition(eventToDisplay);
        }
    }

    public void fetchNewEvents() {
        CalendarFetcher calendarloaderAndFetcher = new CalendarFetcher(appContext);
        if (calendarloaderAndFetcher.fetch()) {
            eventsAdapter.events = calendarloaderAndFetcher.events;
            setCurrentEvent(calendarloaderAndFetcher.eventToDisplay);
            Log.v("GISMO", "current" + calendarloaderAndFetcher.eventToDisplay);
        }
    }
}
