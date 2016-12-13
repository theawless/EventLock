package com.gobbledygook.theawless.eventlock.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.helper.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SettingsFragment extends PreferenceFragment {
    private static final int CALENDAR_READ_REQUEST_CODE = 0;
    private static final int nonEmptyPreferences[] = new int[]{
            R.string.padding_above_key, R.string.padding_below_key, R.string.padding_left_key, R.string.padding_right_key,
            R.string.title_padding_above_key, R.string.title_padding_below_key, R.string.title_padding_left_key, R.string.title_padding_right_key,
            R.string.time_padding_above_key, R.string.time_padding_below_key, R.string.time_padding_left_key, R.string.time_padding_right_key,
            R.string.color_padding_above_key, R.string.color_padding_below_key, R.string.color_padding_left_key, R.string.color_padding_right_key,
            R.string.color_height_key, R.string.color_width_key, R.string.days_till_key,
            //R.string.number_of_events_key,
    };
    //reasons to fucking hate java
    //no initialisers for map or even arraylist wtf bro
    //shitty syntax
    private static final ArrayList<Integer> versionClicks = new ArrayList<>(Arrays.asList(1, 6, 12, 18, 25, 30, 40, 50, 60, 70, 80, 90, 100));
    private static final int versionStrings[] = new int[]{
            R.string.version_count_1, R.string.version_count_6, R.string.version_count_12,
            R.string.version_count_18, R.string.version_count_25, R.string.version_count_30,
            R.string.version_count_40, R.string.version_count_50, R.string.version_count_60,
            R.string.version_count_70, R.string.version_count_80, R.string.version_count_90,
            R.string.version_count_100,
    };
    private int versionClickCount = 0;
    //stored in weakhash map; hence must store them to prevent garbage collection
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<Preference.OnPreferenceChangeListener> preferenceListeners = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.preferences);
        setUpPreferenceCleaners();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_READ_REQUEST_CODE);
        } else {
            createCalendarList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CALENDAR_READ_REQUEST_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.read_permission_error, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                createCalendarList();
            }
        }
    }

    private void setUpPreferenceCleaners() {
        for (int keyId : nonEmptyPreferences) {
            Preference preference = getPreferenceManager().findPreference(getString(keyId));
            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return !newValue.toString().trim().isEmpty();
                }
            };
            preference.setOnPreferenceChangeListener(listener);
            preferenceListeners.add(listener);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getTitleRes()) {
            case R.string.version_title: {
                int index = versionClicks.indexOf(++versionClickCount);
                if (index != -1) {
                    Toast.makeText(getActivity(), versionStrings[index], Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.string.help_title: {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.help_title)
                        .setMessage(R.string.help)
                        .setIcon(R.drawable.eventlock_icon)
                        .setPositiveButton(R.string.okay, null)
                        .show();
                return true;
            }
            case R.string.preset1_title: {
                resetPreferences();
                getPreferenceManager().getSharedPreferences().edit()
                        .putString(Constants.color_position_key, "right")
                        .putString(Constants.color_type_key, "oval")
                        .putString(Constants.color_padding_right_key, "0")
                        .putBoolean(Constants.color_stick_key, false)
                        .putString(Constants.color_height_key, "16")
                        .putString(Constants.color_width_key, "16")
                        .commit();
                return true;
            }
            case R.string.preset2_title: {
                resetPreferences();
                return true;
            }
            case R.string.preset3_title: {
                resetPreferences();
                getPreferenceManager().getSharedPreferences().edit()
                        .putBoolean(Constants.show_color_key, false)
                        .commit();
                return true;
            }
            default: {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }
    }

    private void resetPreferences() {
        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        Set<String> savedSelectedCalendarCalendars = preferences.getStringSet(Constants.selected_calendars_key, Constants.selected_calendars_default);
        String savedDaysTill = preferences.getString(Constants.days_till_key, Constants.days_till_default);
        String saveFreeText = preferences.getString(Constants.free_text_key, Constants.free_text_default);
        preferences.edit().clear().commit();
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
        preferences.edit()
                .putStringSet(Constants.selected_calendars_key, savedSelectedCalendarCalendars)
                .putString(Constants.days_till_key, savedDaysTill)
                .putString(Constants.free_text_key, saveFreeText)
                .commit();
        Toast.makeText(getActivity(), R.string.preset_set, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("MissingPermission")
    public void createCalendarList() {
        ContentResolver resolver = getActivity().getContentResolver();
        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME};
        Cursor cursor = resolver.query(calendarUri, projection, null, null, null);
        List<String> calendarNames = new ArrayList<>();
        List<String> calendarIds = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            calendarNames.add(cursor.getString(1));
            calendarIds.add(Integer.valueOf(cursor.getInt(0)).toString());
        }
        if (cursor != null) {
            cursor.close();
            MultiSelectListPreference selectedCalendarPref = (MultiSelectListPreference) findPreference(Constants.selected_calendars_key);
            selectedCalendarPref.setEntries(calendarNames.toArray(new CharSequence[calendarNames.size()]));
            selectedCalendarPref.setEntryValues(calendarIds.toArray(new CharSequence[calendarNames.size()]));
        }
    }
}
