package com.gobbledygook.theawless.eventlock.gismo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.gobbledygook.theawless.eventlock.helper.Constants;

import static com.gobbledygook.theawless.eventlock.helper.Utils.dpToPixel;

class LayoutManagerBuilder {

    /*
    ------------------------------------------------------------------------------------------------
     Type     Scroll      Handler                                                           Status
    ------------------------------------------------------------------------------------------------
     1*n H     V           grid, vertical, n columns, limit height to one holder                 Y
     1*n H     H           grid, horizontal, 1 row, limit width of holders - divide by n         ?

     m*1 V     V           grid, vertical, 1 column, limit height to m holder                    Y
     m*1 V     H           grid, horizontal, m rows                                              Y

     m*n R                                                                                       N
     -----------------------------------------------------------------------------------------------
    */

    private final GridLayout gridLayout;

    GridLayoutManager gridLayoutManager;
    int recyclerViewHeight = GridLayout.LayoutParams.WRAP_CONTENT;
    int eventViewWidth = GridLayout.LayoutParams.MATCH_PARENT;

    LayoutManagerBuilder(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
    }

    int decideLayoutManager(String orientation, String scroll, int division) {
        Context context = gridLayout.getContext();
        String horizontal = "horizontal", vertical = "vertical";
        if (orientation.equals(horizontal) && scroll.equals(vertical)) {
            gridLayoutManager = new GridLayoutManager(context, division, GridLayoutManager.VERTICAL, false);
            return 1;
        } else if (orientation.equals(horizontal) && scroll.equals(horizontal)) {
            gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
            return division;
        } else if (orientation.equals(vertical) && scroll.equals(vertical)) {
            gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
            return division;
        } else if (orientation.equals(vertical) && scroll.equals(horizontal)) {
            gridLayoutManager = new GridLayoutManager(context, division, GridLayoutManager.HORIZONTAL, false);
        }
        return -1;
    }

    // just a little nasty hack to find how much size recycler view and view holders should take up.
    void findSuggestedMeasurements(final int factor, SharedPreferences preferences) {
        Context gismoContext = gridLayout.getContext();
        FrameLayout frameLayout = new FrameLayout(gismoContext);
        View eventView = new EventViewBuildDirector(gismoContext, preferences).getEventView();
        gridLayout.addView(frameLayout);
        frameLayout.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
        frameLayout.getLayoutParams().height = GridLayout.LayoutParams.WRAP_CONTENT;
        frameLayout.setPadding(
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_left_key, Constants.gismo_padding_left_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_above_key, Constants.gismo_padding_above_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_right_key, Constants.gismo_padding_right_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_below_key, Constants.gismo_padding_below_default))
        );
        frameLayout.addView(eventView);
        eventView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        eventView.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        if (gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            recyclerViewHeight = eventView.getMeasuredHeight() * factor;
        } else {
            eventViewWidth =
                    (gridLayout.getWidth()
                            - dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_left_key, Constants.gismo_padding_left_default))
                            - dpToPixel(gismoContext, preferences.getString(Constants.gismo_padding_right_key, Constants.gismo_padding_right_default))
                    ) / factor;
        }
        gridLayout.removeView(frameLayout);
    }
}
