package com.gobbledygook.theawless.eventlock;


public class PreferenceConsts {
    public final static String preferences = "event_lock_prefs";
    public final static String authority = BuildConfig.APPLICATION_ID + ".preferences";
    public final static String selected_calendars_key = "selected_calendars_preference_key";
    public final static String from_key = "from_preference_key";
    public final static String to_key = "to_preference_key";
    public final static String max_events_key = "max_events_preference_key";
    public final static String time_font_key = "time_font_preference_key";
    public final static String title_font_key = "title_font_preference_key";
    public final static String free_key = "free_preference_key";

    public final static String event_title_key = "event_title_preference_key";
    public final static String event_time_key = "event_time_preference_key";

    public final static String selected_calendars_default = "1";
    public final static String to_default = "0";
    public final static String from_default = "true";
    public final static String time_font_default = "14";
    public final static String title_font_default = "14";
    public final static String max_events_default = "2";
    public final static String free_default = "Free yay!";

    private PreferenceConsts() {
    }
}
