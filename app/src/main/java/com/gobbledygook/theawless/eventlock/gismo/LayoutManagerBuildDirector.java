package com.gobbledygook.theawless.eventlock.gismo;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.widget.GridLayout;

import com.gobbledygook.theawless.eventlock.helper.Constants;

class LayoutManagerBuildDirector {
    private final SharedPreferences preferences;
    private final LayoutManagerBuilder layoutManagerBuilder;

    private int divisionFactor;

    LayoutManagerBuildDirector(GridLayout gridLayout, SharedPreferences preferences) {
        this.preferences = preferences;
        layoutManagerBuilder = new LayoutManagerBuilder(gridLayout);
    }

    void construct() {
        int factor = layoutManagerBuilder.decideLayoutManager(
                preferences.getString(Constants.gismo_layout_direction_key, Constants.gismo_layout_direction_default),
                preferences.getString(Constants.gismo_scroll_direction_key, Constants.gismo_scroll_direction_default),
                Integer.parseInt(preferences.getString(Constants.number_of_events_key, Constants.number_of_events_default))
        );
        if (factor != -1) {
            layoutManagerBuilder.findSuggestedMeasurements(factor, preferences);
        }
        divisionFactor = factor;
    }

    RecyclerView.LayoutManager getLayoutManager() {
        return layoutManagerBuilder.gridLayoutManager;
    }

    int getDivisionFactor() {
        return divisionFactor;
    }

    int getRecyclerViewHeight() {
        return layoutManagerBuilder.recyclerViewHeight;
    }

    int getEventViewWidth() {
        return layoutManagerBuilder.eventViewWidth;
    }
}
