package com.gobbledygook.theawless.eventlock.lockscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XModuleResources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.crossbowffs.remotepreferences.RemotePreferences;
import com.gobbledygook.theawless.eventlock.BuildConfig;
import com.gobbledygook.theawless.eventlock.helper.PreferenceConstants;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LockscreenHook implements IXposedHookZygoteInit, IXposedHookInitPackageResources, IXposedHookLoadPackage, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    private static String modulePath;
    private TextView eventTitleTextView;
    private TextView eventTimeTextView;
    private XModuleResources moduleResources;
    private RemotePreferences preferences;

    private void refreshAllEvents() {
        String title = preferences.getString(PreferenceConstants.event_title_key, null);
        String time = preferences.getString(PreferenceConstants.event_time_key, null);
        //case when the calendar checking service didn't start
        if (title == null || time == null) {
            title = PreferenceConstants.start_text;
            time = "";
        }
        eventTitleTextView.setText(title);
        eventTimeTextView.setText(time);
        //there are no events
        if (TextUtils.isEmpty(title)) {
            String freeText = preferences.getString(PreferenceConstants.free_key, PreferenceConstants.free_default);
            if (TextUtils.isEmpty(freeText)) {
                eventTitleTextView.setVisibility(View.GONE);
            } else {
                eventTitleTextView.setText(freeText);
                eventTimeTextView.setVisibility(View.VISIBLE);
            }
            eventTimeTextView.setVisibility(View.GONE);
        } else {
            eventTimeTextView.setVisibility(View.VISIBLE);
            eventTitleTextView.setVisibility(View.VISIBLE);
        }
        eventTitleTextView.setTextSize(Integer.parseInt(preferences.getString(PreferenceConstants.title_font_key, PreferenceConstants.title_font_default)));
        eventTimeTextView.setTextSize(Integer.parseInt(preferences.getString(PreferenceConstants.time_font_key, PreferenceConstants.time_font_default)));
    }

    private void refreshDisplayEvent() {
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui")) {
            return;
        }
        XposedHelpers.findAndHookMethod("com.android.keyguard.KeyguardStatusView", lpparam.classLoader, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log("EventLock inject views");
                GridLayout self = (GridLayout) param.thisObject;
                Context context = self.getContext();
                LayoutInflater layoutInflater = LayoutInflater.from(self.getContext());
                View view = layoutInflater.inflate(moduleResources.getLayout(moduleResources.getIdentifier("lockscreen", "layout", PACKAGE_NAME)), null);
                self.addView(view);
                eventTitleTextView = (TextView) view.findViewById(moduleResources.getIdentifier("event_title", "id", PACKAGE_NAME));
                eventTimeTextView = (TextView) view.findViewById(moduleResources.getIdentifier("event_time", "id", PACKAGE_NAME));
                XposedBridge.log("EventLock finished injecting");
                preferences = new RemotePreferences(context, PreferenceConstants.authority, PreferenceConstants.preferences);
                preferences.registerOnSharedPreferenceChangeListener(LockscreenHook.this);
                XposedBridge.log("EventLock registered with prefs");
                refreshAllEvents();
            }
        });
        XposedBridge.log("EventLock Xposed module initialized!");
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

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        moduleResources = XModuleResources.createInstance(modulePath, resparam.res);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        modulePath = startupParam.modulePath;
    }
}