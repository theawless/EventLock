package com.gobbledygook.theawless.eventlock.helper;

//used only for when R is not available, like lockscreenhook
public class PreferenceConstants {
    public final static String preferences = "event_lock_preferences";
    public final static String authority = "com.gobbledygook.theawless.eventlock.preferences";

    public final static String selected_calendars_key = "selected_calendars_preference_key";
    public final static String days_till_key = "days_till_preference_key";
    public final static String time_font_size_key = "time_font_size_preference_key";
    public final static String title_font_size_key = "title_font_size_preference_key";
    public final static String time_font_orientation_key = "time_font_orientation_preference_key";
    public final static String title_font_orientation_key = "title_font_orientation_preference_key";
    public final static String free_text_key = "free_text_preference_key";

    public final static String event_to_display_key = "event_to_display_preference_key";
    public final static String event_titles_key = "event_titles_preference_key";
    public final static String event_times_key = "event_times_preference_key";
    public final static String event_long_end_times_key = "event_long_end_times_preference_key";
    public final static String event_colors_key = "event_colors_preference_key";
    public final static String event_descriptions_key = "event_descriptions_preference_key";

    public final static String event_to_display_default = "0";
    public final static String days_till_default = "3";
    public final static String time_font_size_default = "14";
    public final static String title_font_size_default = "14";
    public final static String time_font_orientation_default = "left";
    public final static String title_font_orientation_default = "left";
    public final static String free_text_default = "No more events";
    public final static String padding_from_above_default = "24";
    public final static String show_color_default = "true";


    private PreferenceConstants() {
    }
}