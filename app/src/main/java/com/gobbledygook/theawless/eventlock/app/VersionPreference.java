package com.gobbledygook.theawless.eventlock.app;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.gobbledygook.theawless.eventlock.BuildConfig;

public class VersionPreference extends Preference {
    public VersionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSummary(BuildConfig.VERSION_NAME + '.' + BuildConfig.VERSION_CODE);
    }
}
