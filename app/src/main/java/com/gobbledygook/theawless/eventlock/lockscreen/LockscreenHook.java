package com.gobbledygook.theawless.eventlock.lockscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridLayout;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.gobbledygook.theawless.eventlock.helper.PreferenceConstants;

import org.json.JSONArray;
import org.json.JSONException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LockscreenHook implements IXposedHookLoadPackage, SharedPreferences.OnSharedPreferenceChangeListener {
    private RemotePreferences preferences;
    private LockscreenAdapter adapter = new LockscreenAdapter();
    private LinearLayoutManager layoutManager;

    private void refreshAllEvents() {
        String JSON_titles = preferences.getString(PreferenceConstants.event_titles_key, null);
        String JSON_times = preferences.getString(PreferenceConstants.event_times_key, null);
        String JSON_colors = preferences.getString(PreferenceConstants.event_colors_key, null);
        String JSON_descriptions = preferences.getString(PreferenceConstants.event_descriptions_key, null);
        try {
            adapter.titles = new JSONArray(JSON_titles);
            adapter.times = new JSONArray(JSON_times);
            adapter.colors = new JSONArray(JSON_colors);
            adapter.descriptions = new JSONArray(JSON_descriptions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshDisplayEvent() {
        String positionString = preferences.getString(PreferenceConstants.event_to_display_key, null);
        layoutManager.scrollToPosition(Integer.parseInt(positionString));
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui")) {
            return;
        }
        XposedHelpers.findAndHookMethod("com.android.keyguard.KeyguardStatusView", lpparam.classLoader, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                GridLayout lockcreenGridLayout = (GridLayout) param.thisObject;
                Context context = lockcreenGridLayout.getContext();

                preferences = new RemotePreferences(context, PreferenceConstants.authority, PreferenceConstants.preferences);
                preferences.registerOnSharedPreferenceChangeListener(LockscreenHook.this);
                XposedBridge.log("EventLock registered with prefs");

                RecyclerView recyclerView = new RecyclerView(context);
                layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);

                refreshAllEvents();
                recyclerView.setAdapter(adapter);

                lockcreenGridLayout.addView(recyclerView);
                recyclerView.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
                recyclerView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                XposedBridge.log("EventLock finished injecting");

                refreshDisplayEvent();
            }
        });
        XposedBridge.log("EventLock xposed module initialized!");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceConstants.event_to_display_key)) {
            refreshDisplayEvent();
            return;
        }
        if (!key.equals(PreferenceConstants.selected_calendars_key) && !key.equals(PreferenceConstants.days_till_key)) {
            refreshAllEvents();
        }
    }
}