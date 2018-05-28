package com.gobbledygook.theawless.eventlock.gismo;

import static com.gobbledygook.theawless.eventlock.helper.Utils.dpToPixel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.helper.Enums;

class LayoutManagerHandler {
    private final SharedPreferences preferences;
    private final GridLayout gridLayout;
    private final int divisionFactors[] = new int[]{1 /* external use */, 1 /* internal use */};
    private View eventView;
    private GridLayoutManager gridLayoutManager;
    private int dimensions[][];

    LayoutManagerHandler(GridLayout gridLayout, SharedPreferences preferences) {
        this.preferences = preferences;
        this.gridLayout = gridLayout;
        setupDefaultDimensions();
    }

    private void setupDefaultDimensions() {
        dimensions = new int[][]{
                new int[]{1, 1}, // color
                new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT}, // eventview
                new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT}, // recyclerview
        };
    }

    void construct(Runnable runnable) {
        setupDefaultDimensions();
        Context gismoContext = gridLayout.getContext();
        setupEventView();
        addEventView(new int[]{
                        dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_left_key, Constants.gismo_padding_left_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_above_key, Constants.gismo_padding_above_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_right_key, Constants.gismo_padding_right_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_below_key, Constants.gismo_padding_below_default))
                }
        );
        // trying my best to getHeight lol
        gridLayout.post(runnable);
    }

    private void setupEventView() {
        Context gismoContext = gridLayout.getContext();
        eventView = new EventViewBuildDirector(gismoContext, preferences)
                .setBuilder(
                        new EventViewBuilder(gismoContext) {
                            @Override
                            protected void setupFullContainerRelativeLayout(int[] eventViewDimensions) {
                                super.setupFullContainerRelativeLayout(eventViewDimensions);
                                fullContainerRelativeLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                            }
                        }
                )
                .setInnerDimensions(getInnerDimensions())
                .getEventView();
        TextView titleView = eventView.findViewWithTag(Enums.ItemTag.Title);
        titleView.setTypeface(titleView.getTypeface(), Typeface.BOLD);
        TextView timeView = eventView.findViewWithTag(Enums.ItemTag.Time);
        timeView.setTypeface(timeView.getTypeface(), Typeface.BOLD);
    }

    private void addEventView(int[] gismoPadding) {
        FrameLayout frameLayout = new FrameLayout(gridLayout.getContext());
        frameLayout.addView(eventView);
        gridLayout.addView(frameLayout);
        frameLayout.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
        frameLayout.getLayoutParams().height = GridLayout.LayoutParams.WRAP_CONTENT;
        frameLayout.setPadding(gismoPadding[0], gismoPadding[1], gismoPadding[2], gismoPadding[3]);
    }

    void doAfterPost() {
        findMeasurements();
        gridLayout.removeView((FrameLayout) eventView.getParent());
    }

    // man this is hard! had to try loads of combinations to get both width and height
    private void findMeasurements() {
        Context gismoContext = gridLayout.getContext();
        ((FrameLayout) eventView.getParent()).measure(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        int width = gridLayout.getWidth()
                - dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_left_key, Constants.gismo_padding_left_default))
                - dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_right_key, Constants.gismo_padding_right_default));
        dimensions[Enums.Dimensions.EventView.ordinal()][1] = dimensions[Enums.Dimensions.ColorImageView.ordinal()][1] = eventView.getMeasuredHeight();
        if (getGridLayoutManager().getOrientation() == GridLayoutManager.VERTICAL) {
            dimensions[Enums.Dimensions.RecyclerView.ordinal()][1] = ((FrameLayout) eventView.getParent()).getMeasuredHeight() + (dimensions[Enums.Dimensions.EventView.ordinal()][1] * (divisionFactors[0] - 1));
            dimensions[Enums.Dimensions.ColorImageView.ordinal()][0] = width / divisionFactors[1];
        } else {
            dimensions[Enums.Dimensions.EventView.ordinal()][0] = dimensions[Enums.Dimensions.ColorImageView.ordinal()][0] = width / divisionFactors[0];
        }
    }

    void decideLayoutManager() {
    /*
    ------------------------------------------------------------------------------------------------
     Type     Scroll      Handler                                                             Color
    ------------------------------------------------------------------------------------------------
     1*n H     V           grid, vertical, n columns, limit height to one holder               w/n
     1*n H     H           grid, horizontal, 1 row, limit width of holders - divide by n       w/n

     m*1 V     V           grid, vertical, 1 column, limit height to m holder                  w
     m*1 V     H           grid, horizontal, m rows                                            w
     -----------------------------------------------------------------------------------------------
    */
        String orientation = preferences.getString(Constants.gismo_layout_direction_key, Constants.gismo_layout_direction_default);
        String scroll = preferences.getString(Constants.gismo_scroll_direction_key, Constants.gismo_scroll_direction_default);
        int division = Integer.parseInt(preferences.getString(Constants.number_of_events_key, Constants.number_of_events_default));
        Context context = gridLayout.getContext();
        String horizontal = "horizontal", vertical = "vertical";
        if (orientation.equals(horizontal) && scroll.equals(vertical)) {
            divisionFactors[0] = 1;
            divisionFactors[1] = division;
            gridLayoutManager = new GridLayoutManager(context, division, GridLayoutManager.VERTICAL, false);
        } else if (orientation.equals(horizontal) && scroll.equals(horizontal)) {
            divisionFactors[0] = division;
            divisionFactors[1] = 1;
            gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
        } else if (orientation.equals(vertical) && scroll.equals(vertical)) {
            divisionFactors[0] = division;
            divisionFactors[1] = 1;
            gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
        } else if (orientation.equals(vertical) && scroll.equals(horizontal)) {
            divisionFactors[0] = 1;
            divisionFactors[1] = division;
            gridLayoutManager = new GridLayoutManager(context, division, GridLayoutManager.HORIZONTAL, false);
        }
    }

    GridLayoutManager getGridLayoutManager() {
        return gridLayoutManager;
    }

    int getDivisionFactor() {
        return divisionFactors[0];
    }

    int[] getOuterDimensions() {
        return dimensions[Enums.Dimensions.RecyclerView.ordinal()];
    }

    int[][] getInnerDimensions() {
        return new int[][]{dimensions[Enums.Dimensions.ColorImageView.ordinal()], dimensions[Enums.Dimensions.EventView.ordinal()]};
    }
}
