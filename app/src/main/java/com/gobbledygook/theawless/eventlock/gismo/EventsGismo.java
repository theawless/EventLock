package com.gobbledygook.theawless.eventlock.gismo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.widget.GridLayout;

import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.helper.Enums;

import java.util.ArrayList;

import static com.gobbledygook.theawless.eventlock.helper.Utils.dpToPixel;


public class EventsGismo {
    protected final SharedPreferences preferences;
    private final GridLayout gridLayout;

    private RecyclerView recyclerView = null;
    private EventsAdapter eventsAdapter = null;
    private boolean ready = false;
    private DivisionLinearSnapHelper linearSnapHelper = new DivisionLinearSnapHelper();

    public EventsGismo(GridLayout gridLayout, SharedPreferences preferences) {
        this.preferences = preferences;
        this.gridLayout = gridLayout;
        gridLayout.setNestedScrollingEnabled(true);
    }

    public boolean isReady() {
        return ready;
    }

    public void addRecyclerView() {
        Context gismoContext = gridLayout.getContext();
        recyclerView = new RecyclerView(gismoContext);
        recyclerView.setLayoutParams(new GridLayout.LayoutParams());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setPadding(
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_left_key, Constants.gismo_padding_left_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_above_key, Constants.gismo_padding_above_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_right_key, Constants.gismo_padding_right_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_below_key, Constants.gismo_padding_below_default))
        );
        setupRecyclerView();
        gridLayout.addView(recyclerView);
        linearSnapHelper.attachToRecyclerView(recyclerView);
        ready = true;
    }

    private void setupRecyclerView() {
        LayoutManagerBuildDirector layoutManagerBuildDirector = new LayoutManagerBuildDirector(gridLayout, preferences);
        layoutManagerBuildDirector.construct();
        recyclerView.setLayoutManager(layoutManagerBuildDirector.getLayoutManager());
        recyclerView.getLayoutParams().height = layoutManagerBuildDirector.getRecyclerViewHeight();
        recyclerView.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
        eventsAdapter = new EventsAdapter(preferences, layoutManagerBuildDirector.getEventViewWidth());
        linearSnapHelper.setDivisionFactor(layoutManagerBuildDirector.getDivisionFactor());
        updateCurrentHighlightCache();
        recyclerView.setAdapter(eventsAdapter);
    }

    public void scrollToCurrentEvent() {
        if (eventsAdapter.inRange()) {
            recyclerView.scrollToPosition(eventsAdapter.currentEventIndex);
        }
    }

    //called by update receiver, reset recycler view
    public void notifyUpdatedPreferences() {
        setupRecyclerView();
    }

    //called by update receiver, step 1
    public void deliverNewEvents(ArrayList<String> formattedTitles, ArrayList<String> formattedTimes, ArrayList<String> colors) {
        //add empty event
        if (preferences.getBoolean(Constants.show_free_text_key, Boolean.parseBoolean(Constants.show_free_text_default))) {
            formattedTitles.add(preferences.getString(Constants.free_text_key, Constants.free_text_default));
            formattedTimes.add("");
            colors.add(String.valueOf(Color.TRANSPARENT));
        }
        ArrayList<String> events[] = (ArrayList<String>[]) new ArrayList[3];
        events[Enums.EventInfo.Title.ordinal()] = formattedTitles;
        events[Enums.EventInfo.Time.ordinal()] = formattedTimes;
        events[Enums.EventInfo.Color.ordinal()] = colors;
        eventsAdapter.events = events;
        eventsAdapter.notifyDataSetChanged();
    }

    //called by update receiver, step 2
    public void deliverNewCurrentEvent(int eventToDisplay) {
        if (eventsAdapter.inRange()) {
            eventsAdapter.notifyItemChanged(eventsAdapter.currentEventIndex);
        }
        eventsAdapter.currentEventIndex = eventToDisplay;
        if (eventsAdapter.inRange()) {
            eventsAdapter.notifyItemChanged(eventsAdapter.currentEventIndex);
        }
        scrollToCurrentEvent();
    }

    //cache these because they will be used in onBind on recycler view
    private void updateCurrentHighlightCache() {
        eventsAdapter.currentHighlight = new boolean[]{
                preferences.getBoolean(Constants.current_title_bold_key, Boolean.parseBoolean(Constants.current_title_bold_default)),
                preferences.getBoolean(Constants.current_time_bold_key, Boolean.parseBoolean(Constants.current_time_bold_default)),
                preferences.getBoolean(Constants.current_color_outline_key, Boolean.parseBoolean(Constants.current_color_outline_default)),
        };
    }
}