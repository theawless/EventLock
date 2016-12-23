package com.gobbledygook.theawless.eventlock.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.helper.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsFragment extends PreferenceFragment {
    private static final int CALENDAR_READ_REQUEST_CODE = 0;
    private static final int nonEmptyPreferences[] = new int[]{
            R.string.gismo_padding_above_key, R.string.gismo_padding_below_key, R.string.gismo_padding_left_key, R.string.gismo_padding_right_key,
            R.string.title_padding_above_key, R.string.title_padding_below_key, R.string.title_padding_left_key, R.string.title_padding_right_key,
            R.string.time_padding_above_key, R.string.time_padding_below_key, R.string.time_padding_left_key, R.string.time_padding_right_key,
            R.string.color_padding_above_key, R.string.color_padding_below_key, R.string.color_padding_left_key, R.string.color_padding_right_key,
            R.string.color_height_key, R.string.color_width_key, R.string.days_till_key,
            R.string.number_of_events_key,
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
    //stored in weakhash map; hence must store it to prevent garbage collection
    private Preference.OnPreferenceChangeListener preferenceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return !newValue.toString().trim().isEmpty();
        }
    };
    private PresetHandler presetHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.preferences);
        setUpPreferenceCleaners();
        presetHandler = new PresetHandler(getActivity());
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

    private void createCalendarList() {
        ((CalendarPreference) findPreference(Constants.selected_calendars_key)).getCalendarIds();
    }

    private void setUpPreferenceCleaners() {
        for (int keyId : nonEmptyPreferences) {
            Preference preference = getPreferenceManager().findPreference(getString(keyId));
            preference.setOnPreferenceChangeListener(preferenceListener);
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
            case R.string.multiple_preset1_title: {
                presetHandler.resetPreferences();
                presetHandler.lineLeft();
                return true;
            }
            case R.string.multiple_preset2_title: {
                presetHandler.resetPreferences();
                presetHandler.circleBelow();
                presetHandler.centerText();
                presetHandler.setOrientations("horizontal", "horizontal");
                return true;
            }
            case R.string.multiple_preset3_title: {
                presetHandler.resetPreferences();
                presetHandler.lineLeft();
                presetHandler.setOrientations("horizontal", "horizontal");
                return true;
            }
            case R.string.multiple_preset4_title: {
                presetHandler.resetPreferences();
                presetHandler.circleRight();
                return true;
            }
            case R.string.multiple_preset5_title: {
                presetHandler.resetPreferences();
                presetHandler.lineAbove();
                presetHandler.setOrientations("horizontal", "horizontal");
                return true;
            }
            case R.string.multiple_preset6_title: {
                presetHandler.resetPreferences();
                presetHandler.eclipseAbove();
                presetHandler.centerText();
                return true;
            }
            case R.string.single_preset1_title: {
                presetHandler.resetPreferences();
                presetHandler.circleRight();
                presetHandler.setOrientations("horizontal", "horizontal");
                presetHandler.setMultipleEvents(1);
                return true;
            }
            case R.string.single_preset2_title: {
                presetHandler.resetPreferences();
                presetHandler.setOrientations("horizontal", "horizontal");
                presetHandler.setMultipleEvents(1);
                return true;
            }
            case R.string.single_preset3_title: {
                presetHandler.resetPreferences();
                presetHandler.noColor();
                presetHandler.setOrientations("horizontal", "horizontal");
                presetHandler.setMultipleEvents(1);
                return true;
            }
            default: {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }
    }
}
