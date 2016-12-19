package com.gobbledygook.theawless.eventlock.gismo;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.gobbledygook.theawless.eventlock.helper.Constants;

import static com.gobbledygook.theawless.eventlock.helper.Utils.dpToPixel;

class EventViewBuildDirector {
    private final Context gismoContext;
    private final SharedPreferences preferences;

    private int eventViewWidth;

    EventViewBuildDirector(Context gismoContext, SharedPreferences preferences) {
        this.gismoContext = gismoContext;
        this.preferences = preferences;
    }

    EventViewBuildDirector setEventViewWidth(int eventViewWidth) {
        this.eventViewWidth = eventViewWidth;
        return this;
    }

    View getEventView() {
        EventViewBuilder builder = new EventViewBuilder(gismoContext);
        builder.setUpFullContainerRelativeLayout(eventViewWidth);
        builder.setUpTextContainerRelativeLayout(preferences.getString(Constants.text_position_key, Constants.text_position_default));
        builder.setUpTitleTextView(
                dpToPixel(gismoContext, preferences.getString(Constants.title_padding_left_key, Constants.title_padding_left_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.title_padding_above_key, Constants.title_padding_above_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.title_padding_right_key, Constants.title_padding_right_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.title_padding_below_key, Constants.title_padding_below_default)),
                Integer.parseInt(preferences.getString(Constants.title_font_size_key, Constants.title_font_size_default)),
                preferences.getString(Constants.title_alignment_key, Constants.title_alignment_default)
        );
        builder.setUpTimeTextView(
                dpToPixel(gismoContext, preferences.getString(Constants.time_padding_left_key, Constants.time_padding_left_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.time_padding_above_key, Constants.time_padding_above_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.time_padding_right_key, Constants.time_padding_right_default)),
                dpToPixel(gismoContext, preferences.getString(Constants.time_padding_below_key, Constants.time_padding_below_default)),
                Integer.parseInt(preferences.getString(Constants.time_font_size_key, Constants.time_font_size_default)),
                preferences.getString(Constants.time_alignment_key, Constants.time_alignment_default)
        );
        if (preferences.getBoolean(Constants.show_color_key, Boolean.parseBoolean(Constants.show_color_default))) {
            builder.setUpColorImageView(
                    dpToPixel(gismoContext, preferences.getString(Constants.color_padding_left_key, Constants.color_padding_left_default)),
                    dpToPixel(gismoContext, preferences.getString(Constants.color_padding_above_key, Constants.color_padding_above_default)),
                    dpToPixel(gismoContext, preferences.getString(Constants.color_padding_right_key, Constants.color_padding_right_default)),
                    dpToPixel(gismoContext, preferences.getString(Constants.color_padding_below_key, Constants.color_padding_below_default)),
                    preferences.getString(Constants.color_type_key, Constants.color_type_default),
                    dpToPixel(gismoContext, preferences.getString(Constants.color_height_key, Constants.color_height_default)),
                    dpToPixel(gismoContext, preferences.getString(Constants.color_width_key, Constants.color_width_default)),
                    preferences.getString(Constants.color_position_key, Constants.color_position_default),
                    preferences.getBoolean(Constants.color_stick_key, Boolean.parseBoolean(Constants.color_stick_default))
            );
        }
        return builder.fullContainerRelativeLayout;
    }
}

