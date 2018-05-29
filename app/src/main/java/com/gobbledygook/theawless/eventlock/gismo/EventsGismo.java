package com.gobbledygook.theawless.eventlock.gismo;

import static com.gobbledygook.theawless.eventlock.helper.Utils.dpToPixel;
import static com.gobbledygook.theawless.eventlock.helper.Utils.preventParentTouchTheft;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.widget.GridLayout;

import com.gobbledygook.theawless.eventlock.helper.Constants;

import java.util.ArrayList;
import java.util.Arrays;


public class EventsGismo {
    protected final SharedPreferences preferences;
    private final GridLayout gridLayout;
    private final DivisionLinearSnapHelper snapHelper;
    private final LayoutManagerHandler layoutManagerHandler;
    private final RecyclerView recyclerView;
    private final EventsAdapter eventsAdapter;

    public EventsGismo(GridLayout gridLayout, SharedPreferences preferences) {
        this.preferences = preferences;
        this.gridLayout = gridLayout;
        recyclerView = new RecyclerView(gridLayout.getContext());
        snapHelper = new DivisionLinearSnapHelper();
        layoutManagerHandler = new LayoutManagerHandler(gridLayout, preferences);
        eventsAdapter = new EventsAdapter(preferences);
        eventsAdapter.setupInnerDimensions(layoutManagerHandler.getInnerDimensions());
    }

    public void addRecyclerView() {
        gridLayout.addView(recyclerView);
        setupRecyclerView();
        recyclerView.setAdapter(eventsAdapter);
        snapHelper.attachToRecyclerView(recyclerView);
        preventParentTouchTheft(recyclerView);
    }

    private void setupRecyclerView() {
        updateCache();
        Context gismoContext = gridLayout.getContext();
        recyclerView.setPadding(
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_left_key, Constants.gismo_padding_left_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_above_key, Constants.gismo_padding_above_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_right_key, Constants.gismo_padding_right_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_below_key, Constants.gismo_padding_below_default))
        );
        layoutManagerHandler.decideLayoutManager();
        snapHelper.setDivisionFactor(layoutManagerHandler.getDivisionFactor());
        recyclerView.setLayoutManager(layoutManagerHandler.getGridLayoutManager());
        layoutManagerHandler.construct(new Runnable() {
            @Override
            public void run() {
                layoutManagerHandler.doAfterPost();
                doAfterPost();
            }
        });
    }

    private void doAfterPost() {
        eventsAdapter.setupInnerDimensions(layoutManagerHandler.getInnerDimensions());
        int[] recyclerViewDimensions = layoutManagerHandler.getOuterDimensions();
        recyclerView.getLayoutParams().width = recyclerViewDimensions[0];
        recyclerView.getLayoutParams().height = recyclerViewDimensions[1];
        recyclerView.setAdapter(eventsAdapter);
    }

    //--------------------------------------------------------------------------------------------//

    // when screen gets off
    public void scrollToCurrentEvent() {
        if (eventsAdapter.inRange() && recyclerView.getAdapter() != null) {
            recyclerView.scrollToPosition(eventsAdapter.currentEventIndex);
        }
    }

    // called by update receiver, reset recycler view
    public void notifyUpdatedPreferences() {
        setupRecyclerView();
    }

    // called by update receiver, step 1
    public void deliverNewEvents(ArrayList<String> formattedTitles, ArrayList<String> formattedTimes, ArrayList<String> colors) {
        // add empty event
        if (preferences.getBoolean(Constants.show_free_text_key, Boolean.parseBoolean(Constants.show_free_text_default))) {
            formattedTitles.add(preferences.getString(Constants.free_text_key, Constants.free_text_default));
            formattedTimes.add("");
            colors.add(String.valueOf(Color.TRANSPARENT));
        }
        eventsAdapter.events = new ArrayList<>(Arrays.asList(formattedTitles, formattedTimes, colors));
        eventsAdapter.notifyDataSetChanged();
    }

    // called by update receiver, step 2
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

    // cache these because they will be used in onBind on recycler view
    private void updateCache() {
        eventsAdapter.currentHighlight = new boolean[]{
                preferences.getBoolean(Constants.current_title_bold_key, Boolean.parseBoolean(Constants.current_title_bold_default)),
                preferences.getBoolean(Constants.current_time_bold_key, Boolean.parseBoolean(Constants.current_time_bold_default)),
                preferences.getBoolean(Constants.current_color_outline_key, Boolean.parseBoolean(Constants.current_color_outline_default)),
                preferences.getBoolean(Constants.dark_mode_key, Boolean.parseBoolean(Constants.dark_mode_default))
        };
    }
}
