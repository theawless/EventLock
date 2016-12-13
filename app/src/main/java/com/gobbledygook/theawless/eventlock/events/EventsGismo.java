package com.gobbledygook.theawless.eventlock.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;

import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.helper.Enums;

import java.util.ArrayList;


public class EventsGismo {
    protected final SharedPreferences preferences;

    private RecyclerView recyclerView = null;
    private EventsAdapter eventsAdapter = null;

    public EventsGismo(SharedPreferences preferences) {
        this.preferences = preferences;
        eventsAdapter = new EventsAdapter(preferences);
        //cache these because they will be used in onBind on recycler view
        eventsAdapter.currentHighlight = new boolean[]{
                preferences.getBoolean(Constants.current_title_bold_key, Boolean.parseBoolean(Constants.current_title_bold_default)),
                preferences.getBoolean(Constants.current_time_bold_key, Boolean.parseBoolean(Constants.current_time_bold_default)),
                preferences.getBoolean(Constants.current_color_outline_key, Boolean.parseBoolean(Constants.current_color_outline_default)),
        };
    }

    public RecyclerView getRecyclerView(Context gismoContext) {
        if (recyclerView == null) {
            recyclerView = new RecyclerView(gismoContext);
            recyclerView.setLayoutManager(new LinearLayoutManager(gismoContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(eventsAdapter);
            new LinearSnapHelper().attachToRecyclerView(recyclerView);
        }
        return recyclerView;
    }

    public void scrollToCurrentEvent() {
        if (recyclerView != null) {
            recyclerView.getLayoutManager().scrollToPosition(eventsAdapter.currentEventIndex);
        }
    }

    //called by update receiver, step 1
    public void deliverNewEvents(ArrayList<String> formattedTitles, ArrayList<String> formattedTimes, ArrayList<String> colors) {
        //add empty event
        formattedTitles.add(preferences.getString(Constants.free_text_key, Constants.free_text_default));
        formattedTimes.add("");
        colors.add(String.valueOf(Color.TRANSPARENT));

        ArrayList<String>[] events = (ArrayList<String>[]) new ArrayList[3];
        events[Enums.EventInfo.Title.ordinal()] = formattedTitles;
        events[Enums.EventInfo.Time.ordinal()] = formattedTimes;
        events[Enums.EventInfo.Color.ordinal()] = colors;
        eventsAdapter.events = events;
        eventsAdapter.notifyDataSetChanged();
    }

    //called by update receiver, step 2
    public void deliverNewCurrentEvent(int eventToDisplay) {
        eventsAdapter.notifyItemChanged(eventsAdapter.currentEventIndex);
        eventsAdapter.currentEventIndex = eventToDisplay;
        eventsAdapter.notifyItemChanged(eventsAdapter.currentEventIndex);
        scrollToCurrentEvent();
    }

    //called by update receiver, force recycler view to redraw
    public void notifyUpdatedPreferences() {
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(eventsAdapter);
        eventsAdapter.currentHighlight = new boolean[]{
                preferences.getBoolean(Constants.current_title_bold_key, Boolean.parseBoolean(Constants.current_title_bold_default)),
                preferences.getBoolean(Constants.current_time_bold_key, Boolean.parseBoolean(Constants.current_time_bold_default)),
                preferences.getBoolean(Constants.current_color_outline_key, Boolean.parseBoolean(Constants.current_color_outline_default)),
        };
    }
}