package com.gobbledygook.theawless.eventlock.app;

import android.Manifest;
import android.content.ContentResolver;
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

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends PreferenceFragment {
    private static final int CALENDAR_READ_REQUEST_CODE = 0;
    private final static int nonEmptyPreferences[] = new int[]{
            R.string.padding_above_key, R.string.padding_below_key, R.string.padding_left_key, R.string.padding_right_key,
            R.string.title_padding_above_key, R.string.title_padding_below_key, R.string.title_padding_left_key, R.string.title_padding_right_key,
            R.string.time_padding_above_key, R.string.time_padding_below_key, R.string.time_padding_left_key, R.string.time_padding_right_key,
            R.string.color_padding_above_key, R.string.color_padding_below_key, R.string.color_padding_left_key, R.string.color_padding_right_key,
            R.string.color_height_key, R.string.color_width_key, R.string.color_radius_key,
            R.string.days_till_key, R.string.number_of_events_key,
    };
    private int versionClickCount = 0;
    //stored in weakhash map; hence must store them to prevent garbage collection
    private ArrayList<Preference.OnPreferenceChangeListener> preferenceListeners = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setUpPreferenceCleaners();
        setUpConditionalPreferences();
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

    private void setUpConditionalPreferences() {
        final PreferenceManager preferenceManager = getPreferenceManager();
        boolean ovalSet = preferenceManager.getSharedPreferences().getString(getString(R.string.color_type_key), getString(R.string.color_type_default)).equals(getResources().getStringArray(R.array.color_type_entvals)[0]);
        preferenceManager.findPreference(getString(R.string.color_height_key)).setEnabled(!ovalSet);
        preferenceManager.findPreference(getString(R.string.color_width_key)).setEnabled(!ovalSet);
        preferenceManager.findPreference(getString(R.string.color_radius_key)).setEnabled(ovalSet);
        preferenceManager.findPreference(getString(R.string.color_type_key)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean ovalSet = newValue.toString().equals(getResources().getStringArray(R.array.color_type_entvals)[0]);
                preferenceManager.findPreference(getString(R.string.color_height_key)).setEnabled(!ovalSet);
                preferenceManager.findPreference(getString(R.string.color_width_key)).setEnabled(!ovalSet);
                preferenceManager.findPreference(getString(R.string.color_radius_key)).setEnabled(ovalSet);
                return true;
            }
        });
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getTitleRes() == R.string.version_title) {
            switch (++versionClickCount) {
                case 1: {
                    Toast.makeText(getActivity(), R.string.version_count_three, Toast.LENGTH_SHORT).show();
                    break;
                }
                case 6: {
                    Toast.makeText(getActivity(), R.string.version_count_six, Toast.LENGTH_SHORT).show();
                    break;
                }
                case 12: {
                    Toast.makeText(getActivity(), R.string.version_count_twelve, Toast.LENGTH_SHORT).show();
                    break;
                }
                case 18: {
                    Toast.makeText(getActivity(), R.string.version_count_eighteen, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
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
            MultiSelectListPreference selectedCalendarPref = (MultiSelectListPreference) findPreference(getString(R.string.selected_calendars_key));
            selectedCalendarPref.setEntries(calendarNames.toArray(new CharSequence[calendarNames.size()]));
            selectedCalendarPref.setEntryValues(calendarIds.toArray(new CharSequence[calendarNames.size()]));
        }
    }
}
