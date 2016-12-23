package com.gobbledygook.theawless.eventlock.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.helper.Constants;

import java.util.Set;

class PresetHandler {
    private Context context;
    private SharedPreferences preferences;

    PresetHandler(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void resetPreferences() {
        Set<String> savedSelectedCalendarCalendars = preferences.getStringSet(Constants.selected_calendars_key, Constants.selected_calendars_default);
        String savedDaysTill = preferences.getString(Constants.days_till_key, Constants.days_till_default);
        String saveFreeText = preferences.getString(Constants.free_text_key, Constants.free_text_default);
        boolean saveShowFreeEvent = preferences.getBoolean(Constants.show_free_text_key, Boolean.parseBoolean(Constants.show_free_text_default));
        preferences.edit().clear().commit();
        PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
        preferences.edit()
                .putStringSet(Constants.selected_calendars_key, savedSelectedCalendarCalendars)
                .putString(Constants.days_till_key, savedDaysTill)
                .putString(Constants.free_text_key, saveFreeText)
                .putBoolean(Constants.show_free_text_key, saveShowFreeEvent)
                .commit();
        Toast.makeText(context, R.string.preset_set, Toast.LENGTH_SHORT).show();
    }

    void setMultipleEvents(int n) {
        preferences.edit()
                .putString(Constants.number_of_events_key, String.valueOf(n))
                .commit();
    }

    void setOrientations(String layout, String scroll) {
        preferences.edit()
                .putString(Constants.gismo_layout_direction_key, layout)
                .putString(Constants.gismo_scroll_direction_key, scroll)
                .commit();
    }

    //already the default state. this function here just to help with my minor OCD
    void lineLeft() {

    }

    void lineAbove() {
        preferences.edit()
                .putString(Constants.color_type_key, "rectangle")
                .putString(Constants.color_padding_right_key, "0")
                .putString(Constants.color_position_key, "up")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putBoolean(Constants.color_anti_snap_width_key, false)
                .commit();
    }

    void circleBelow() {
        preferences.edit()
                .putString(Constants.color_position_key, "down")
                .putString(Constants.color_type_key, "oval")
                .putString(Constants.color_padding_right_key, "0")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putString(Constants.color_height_key, "16")
                .putString(Constants.color_width_key, "16")
                .putBoolean(Constants.color_stick_key, true)
                .commit();
    }

    void circleRight() {
        preferences.edit()
                .putString(Constants.color_position_key, "right")
                .putString(Constants.color_type_key, "oval")
                .putString(Constants.color_padding_right_key, "0")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putString(Constants.color_height_key, "16")
                .putString(Constants.color_width_key, "16")
                .putBoolean(Constants.color_stick_key, false)
                .commit();
    }

    void eclipseAbove() {
        preferences.edit()
                .putString(Constants.color_position_key, "up")
                .putString(Constants.color_type_key, "oval")
                .putString(Constants.color_padding_right_key, "0")
                .putBoolean(Constants.color_anti_snap_height_key, true)
                .putBoolean(Constants.color_anti_snap_width_key, false)
                .putBoolean(Constants.color_stick_key, true)
                .commit();
    }

    void noColor() {
        preferences.edit()
                .putBoolean(Constants.show_color_key, false)
                .commit();
    }

    void centerText() {
        preferences.edit()
                .putString(Constants.text_position_key, "center")
                .putString(Constants.time_alignment_key, "center")
                .putString(Constants.title_alignment_key, "center")
                .commit();
    }
}