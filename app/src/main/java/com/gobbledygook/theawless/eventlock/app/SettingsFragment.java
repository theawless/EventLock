package com.gobbledygook.theawless.eventlock.app;

import android.Manifest;
import android.annotation.SuppressLint;
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

public class SettingsFragment extends PreferenceFragment {
    private static final int CALENDAR_READ_REQUEST_CODE = 0;
    private static final int nonEmptyPreferences[] = new int[]{
            R.string.gismo_padding_above_key, R.string.gismo_padding_below_key, R.string.gismo_padding_left_key, R.string.gismo_padding_right_key,
            R.string.title_padding_above_key, R.string.title_padding_below_key, R.string.title_padding_left_key, R.string.title_padding_right_key,
            R.string.time_padding_above_key, R.string.time_padding_below_key, R.string.time_padding_left_key, R.string.time_padding_right_key,
            R.string.color_padding_above_key, R.string.color_padding_below_key, R.string.color_padding_left_key, R.string.color_padding_right_key,
            R.string.color_height_key, R.string.color_width_key, R.string.days_till_key,
            R.string.number_of_events_key, R.string.title_font_size_key, R.string.time_font_size_key,
    };

    private final Preference.OnPreferenceChangeListener preferenceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return !newValue.toString().trim().isEmpty();
        }
    };
    private int versionClickCount = 0;
    private PresetMaker presetMaker;

    @SuppressLint("WorldReadableFiles")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.preferences);
        presetMaker = new PresetMaker(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpPreferenceCleaners();
        permissionCheck();
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_READ_REQUEST_CODE);
        } else {
            createCalendarList();
        }
    }

    private void refreshUI() {
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
        permissionCheck();
        setUpPreferenceCleaners();
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
            if (preference != null) {
                preference.setOnPreferenceChangeListener(preferenceListener);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getTitleRes()) {
            case R.string.version_title: {
                String resourceName = "version_count_" + String.valueOf(++versionClickCount);
                int id = getResources().getIdentifier(resourceName, "string", getActivity().getPackageName());
                if (id != 0) {
                    Toast.makeText(getActivity(), getResources().getString(id), Toast.LENGTH_SHORT).show();
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
                presetMaker.begin()
                        .lineLeft()
                        .makeTextSpaceVertically()
                        .setOrientations("vertical", "vertical")
                        .end();
                break;
            }
            case R.string.multiple_preset2_title: {
                presetMaker.begin()
                        .circleBelow()
                        .makeColorSpaceVertically()
                        .centerText()
                        .end();
                break;
            }
            case R.string.multiple_preset3_title: {
                presetMaker.begin()
                        .lineLeft()
                        .end();
                break;
            }
            case R.string.multiple_preset4_title: {
                presetMaker.begin()
                        .circleRight()
                        .makeTextSpaceVertically()
                        .makeColorSpaceVertically()
                        .setOrientations("vertical", "vertical")
                        .end();
                break;
            }
            case R.string.multiple_preset5_title: {
                presetMaker.begin()
                        .lineAbove()
                        .end();
                break;
            }
            case R.string.multiple_preset6_title: {
                presetMaker.begin()
                        .eclipseAbove()
                        .setOrientations("vertical", "vertical")
                        .centerText()
                        .makeTextSpaceVertically()
                        .end();
                break;
            }
            case R.string.single_preset1_title: {
                presetMaker.begin()
                        .circleRight()
                        .setMultipleEvents(1)
                        .makeTextBig()
                        .end();
                break;
            }
            case R.string.single_preset2_title: {
                presetMaker.begin()
                        .setMultipleEvents(1)
                        .makeTextBig()
                        .end();
                break;
            }
            case R.string.single_preset3_title: {
                presetMaker.begin()
                        .noColor()
                        .setMultipleEvents(1)
                        .makeTextBig()
                        .end();
                break;
            }
            default: {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }
        // ends up here if one of the presets is clicked, otherwise returns early
        refreshUI();
        return true;
    }
}
