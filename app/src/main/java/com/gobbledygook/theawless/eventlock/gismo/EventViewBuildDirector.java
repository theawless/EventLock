package com.gobbledygook.theawless.eventlock.gismo;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.helper.Enums;

import static com.gobbledygook.theawless.eventlock.helper.Utils.dpToPixel;

class EventViewBuildDirector {
    private final Context gismoContext;
    private final SharedPreferences preferences;
    private int[][] innerDimensions;
    private EventViewBuilder builder;

    EventViewBuildDirector(Context gismoContext, SharedPreferences preferences) {
        this.gismoContext = gismoContext;
        this.preferences = preferences;
    }

    EventViewBuildDirector setBuilder(EventViewBuilder eventViewBuilder) {
        this.builder = eventViewBuilder;
        return this;
    }

    EventViewBuildDirector setInnerDimensions(int[][] innerDimensions) {
        this.innerDimensions = innerDimensions;
        return this;
    }

    View getEventView() {
        builder.setupFullContainerRelativeLayout(innerDimensions[Enums.Dimensions.EventView.ordinal()]);
        builder.setupTextContainerLinearLayout();
        builder.setupTitleTextView(
                new int[]{
                        dpToPixel(gismoContext, preferences.getString(Constants.title_padding_left_key, Constants.title_padding_left_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.title_padding_above_key, Constants.title_padding_above_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.title_padding_right_key, Constants.title_padding_right_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.title_padding_below_key, Constants.title_padding_below_default)),
                },
                Integer.parseInt(preferences.getString(Constants.title_font_size_key, Constants.title_font_size_default)),
                preferences.getString(Constants.title_alignment_key, Constants.title_alignment_default),
                preferences.getBoolean(Constants.dark_mode_key, Boolean.parseBoolean(Constants.dark_mode_default))
        );
        builder.setupTimeTextView(
                new int[]{
                        dpToPixel(gismoContext, preferences.getString(Constants.time_padding_left_key, Constants.time_padding_left_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.time_padding_above_key, Constants.time_padding_above_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.time_padding_right_key, Constants.time_padding_right_default)),
                        dpToPixel(gismoContext, preferences.getString(Constants.time_padding_below_key, Constants.time_padding_below_default)),
                },
                Integer.parseInt(preferences.getString(Constants.time_font_size_key, Constants.time_font_size_default)),
                preferences.getString(Constants.time_alignment_key, Constants.time_alignment_default),
                preferences.getBoolean(Constants.dark_mode_key, Boolean.parseBoolean(Constants.dark_mode_default))
        );
        if (preferences.getBoolean(Constants.show_color_key, Boolean.parseBoolean(Constants.show_color_default))) {
            builder.setupColorImageView(
                    new int[]{dpToPixel(gismoContext, preferences.getString(Constants.color_padding_left_key, Constants.color_padding_left_default)),
                            dpToPixel(gismoContext, preferences.getString(Constants.color_padding_above_key, Constants.color_padding_above_default)),
                            dpToPixel(gismoContext, preferences.getString(Constants.color_padding_right_key, Constants.color_padding_right_default)),
                            dpToPixel(gismoContext, preferences.getString(Constants.color_padding_below_key, Constants.color_padding_below_default)),
                    },
                    preferences.getString(Constants.color_type_key, Constants.color_type_default),
                    new int[]{dpToPixel(gismoContext, preferences.getString(Constants.color_width_key, Constants.color_width_default)),
                            dpToPixel(gismoContext, preferences.getString(Constants.color_height_key, Constants.color_height_default)),
                    },
                    new boolean[]{preferences.getBoolean(Constants.color_anti_snap_width_key, Boolean.parseBoolean(Constants.color_anti_width_default)),
                            preferences.getBoolean(Constants.color_anti_snap_height_key, Boolean.parseBoolean(Constants.color_anti_snap_height_default)),
                    },
                    innerDimensions[Enums.Dimensions.ColorImageView.ordinal()]
            );
        }
        builder.setupPositions(
                preferences.getString(Constants.text_position_key, Constants.text_position_default),
                preferences.getString(Constants.color_position_key, Constants.color_position_default),
                preferences.getBoolean(Constants.color_stick_key, Boolean.parseBoolean(Constants.color_stick_default))
        );
        return builder.fullContainerRelativeLayout;
    }
}
