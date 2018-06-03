package com.gobbledygook.theawless.eventlock.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.helper.Constants;

import java.util.Set;

class PresetMaker {
    private final Context context;
    private final SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    PresetMaker(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    PresetMaker begin() {
        resetPreferences();
        editor = preferences.edit();
        removeTextSpaceVertically();
        setOrientations("horizontal", "horizontal");
        return this;
    }

    private PresetMaker resetPreferences() {
        SavedState savedState = new SavedState();
        savedState.saveToMemory();
        editor = preferences.edit();
        savedState.reset();
        editor = preferences.edit();
        savedState.loadBack();
        editor.commit();
        return this;
    }

    void end() {
        editor.commit();
        Toast.makeText(context, R.string.preset_set, Toast.LENGTH_SHORT).show();
    }

    private PresetMaker removeTextSpaceVertically() {
        editor.putString(Constants.time_padding_below_key, "0");
        return this;
    }

    PresetMaker makeTextSpaceVertically() {
        editor.putString(Constants.time_padding_below_key, "3");
        return this;
    }

    PresetMaker makeColorSpaceVertically() {
        editor.putString(Constants.color_padding_below_key, "3");
        return this;
    }

    PresetMaker setMultipleEvents(int n) {
        editor.putString(Constants.number_of_events_key, String.valueOf(n));
        return this;
    }

    PresetMaker setOrientations(String layout, String scroll) {
        editor.putString(Constants.gismo_layout_direction_key, layout)
                .putString(Constants.gismo_scroll_direction_key, scroll);
        return this;
    }

    PresetMaker lineLeft() {
        return this;
    }

    PresetMaker lineAbove() {
        editor.putString(Constants.color_type_key, "rectangle")
                .putString(Constants.color_padding_right_key, "0")
                .putString(Constants.color_position_key, "up")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putBoolean(Constants.color_anti_snap_width_key, false);
        return this;
    }

    PresetMaker circleBelow() {
        editor.putString(Constants.color_position_key, "down")
                .putString(Constants.color_type_key, "oval")
                .putString(Constants.color_padding_right_key, "0")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putString(Constants.color_height_key, "16")
                .putString(Constants.color_width_key, "16")
                .putString(Constants.color_padding_above_key, "3")
                .putBoolean(Constants.color_stick_key, true);
        return this;
    }

    PresetMaker circleRight() {
        editor.putString(Constants.color_position_key, "right")
                .putString(Constants.color_type_key, "oval")
                .putString(Constants.color_padding_right_key, "0")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putString(Constants.color_height_key, "16")
                .putString(Constants.color_width_key, "16")
                .putBoolean(Constants.color_stick_key, false);
        return this;
    }

    PresetMaker eclipseAbove() {
        editor.putString(Constants.color_position_key, "up")
                .putString(Constants.color_type_key, "oval")
                .putString(Constants.color_padding_right_key, "0")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putBoolean(Constants.color_anti_snap_width_key, false)
                .putBoolean(Constants.color_stick_key, true);
        return this;
    }

    PresetMaker noColor() {
        editor.putBoolean(Constants.show_color_key, false);
        return this;
    }

    PresetMaker centerText() {
        editor.putString(Constants.text_position_key, "center")
                .putString(Constants.time_alignment_key, "center")
                .putString(Constants.title_alignment_key, "center");
        return this;
    }

    PresetMaker makeTextBig() {
        editor.putString(Constants.title_font_size_key, "16")
                .putString(Constants.time_font_size_key, "14");
        return this;
    }

    private class SavedState {
        private Set<String> savedSelectedCalendarCalendars;
        private String savedDaysTill;

        void loadBack() {
            editor.putStringSet(Constants.selected_calendars_key, savedSelectedCalendarCalendars)
                    .putString(Constants.days_till_key, savedDaysTill);
        }

        void saveToMemory() {
            savedSelectedCalendarCalendars = preferences.getStringSet(Constants.selected_calendars_key, Constants.selected_calendars_default);
            savedDaysTill = preferences.getString(Constants.days_till_key, Constants.days_till_default);
        }

        void reset() {
            editor.clear().commit();
            PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
        }
    }
}
