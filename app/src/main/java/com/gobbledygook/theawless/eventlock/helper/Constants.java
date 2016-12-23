package com.gobbledygook.theawless.eventlock.helper;

import com.gobbledygook.theawless.eventlock.BuildConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    //intent filters

    public static final String events_update = BuildConfig.APPLICATION_ID + "." + "EventsUpdate";
    public static final String current_event_update = BuildConfig.APPLICATION_ID + "." + "CurrentEventUpdate";
    public static final String looks_update = BuildConfig.APPLICATION_ID + "." + "LooksUpdate";

    //intent extras

    public static final String formatted_titles = "formattedTitles";
    public static final String formatted_times = "formattedTimes";
    public static final String colors = "colors";
    public static final String begin_times = "beginTimes";
    public static final String end_times = "endTimes";
    public static final String current_event = "currentEvent";

    //keys

    public static final String selected_calendars_key = "selected_calendars";
    public static final String days_till_key = "days_till";

    public static final String text_position_key = "text_position";

    public static final String time_alignment_key = "time_alignment";
    public static final String time_font_size_key = "time_font_size";
    public static final String time_padding_above_key = "time_padding_above";
    public static final String time_padding_below_key = "time_padding_below";
    public static final String time_padding_left_key = "time_padding_left";
    public static final String time_padding_right_key = "time_padding_right";
    public static final String time_format_key = "time_format";

    public static final String title_alignment_key = "title_alignment";
    public static final String title_font_size_key = "title_font_size";
    public static final String title_padding_above_key = "title_padding_above";
    public static final String title_padding_below_key = "title_padding_below";
    public static final String title_padding_left_key = "title_padding_left";
    public static final String title_padding_right_key = "title_padding_right";

    public static final String free_text_key = "free_text";
    public static final String show_free_text_key = "show_free_text";
    public static final String number_of_events_key = "number_of_events";
    public static final String gismo_scroll_direction_key = "gismo_scroll";
    public static final String gismo_layout_direction_key = "gismo_layout";
    public static final String gismo_padding_above_key = "gismo_padding_above";
    public static final String gismo_padding_below_key = "gismo_padding_below";
    public static final String gismo_padding_left_key = "gismo_padding_left";
    public static final String gismo_padding_right_key = "gismo_padding_right";

    public static final String color_position_key = "color_position";
    public static final String show_color_key = "show_color";
    public static final String color_stick_key = "color_stick";
    public static final String color_type_key = "color_type";
    public static final String color_anti_snap_height_key = "color_snap_height";
    public static final String color_anti_snap_width_key = "color_snap_width";
    public static final String color_height_key = "color_height";
    public static final String color_width_key = "color_width";
    public static final String color_padding_above_key = "color_padding_above";
    public static final String color_padding_below_key = "color_padding_below";
    public static final String color_padding_left_key = "color_padding_left";
    public static final String color_padding_right_key = "color_padding_right";

    public static final String current_title_bold_key = "current_title_bold";
    public static final String current_time_bold_key = "current_time_bold";
    public static final String current_color_outline_key = "current_color_outline";

    //defaults
    public static final Set<String> selected_calendars_default = new HashSet<>(Collections.singletonList("1"));

    public static final String days_till_default = "3";

    public static final String text_position_default = "left";

    public static final String time_font_size_default = "14";
    public static final String time_alignment_default = "left";
    public static final String time_padding_above_default = "0";
    public static final String time_padding_below_default = "0";
    public static final String time_padding_left_default = "0";
    public static final String time_padding_right_default = "0";
    public static final String time_format_default = "auto";

    public static final String title_font_size_default = "16";
    public static final String title_alignment_default = "left";
    public static final String title_padding_above_default = "0";
    public static final String title_padding_below_default = "0";
    public static final String title_padding_left_default = "0";
    public static final String title_padding_right_default = "0";

    public static final String free_text_default = "No more events";
    public static final String show_free_text_default = "true";

    public static final String number_of_events_default = "2";
    public static final String gismo_scroll_direction_default = "horizontal";
    public static final String gismo_layout_direction_default = "horizontal";
    public static final String gismo_padding_above_default = "6";
    public static final String gismo_padding_below_default = "0";
    public static final String gismo_padding_left_default = "24";
    public static final String gismo_padding_right_default = "24";

    public static final String color_position_default = "left";
    public static final String show_color_default = "true";
    public static final String color_stick_default = "true";
    public static final String color_type_default = "rectangle";
    public static final String color_anti_snap_height_default = "true";
    public static final String color_anti_width_default = "false";
    public static final String color_height_default = "35";
    public static final String color_width_default = "3";
    public static final String color_padding_above_default = "0";
    public static final String color_padding_below_default = "0";
    public static final String color_padding_left_default = "0";
    public static final String color_padding_right_default = "8";

    public static final String current_title_bold_default = "true";
    public static final String current_time_bold_default = "false";
    public static final String current_color_outline_default = "true";

    private Constants() {
    }
}
