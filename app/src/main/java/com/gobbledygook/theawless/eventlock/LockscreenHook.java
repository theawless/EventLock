package com.gobbledygook.theawless.eventlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XModuleResources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.crossbowffs.remotepreferences.RemotePreferences;

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
    private static String sModulePath;
    private TextView eventTitleTextView;
    private TextView eventTimeTextView;
    private XModuleResources moduleRes;
    private RemotePreferences preferences;

    private void refreshShownEvents() {
        XposedBridge.log("refresh");
        String title = preferences.getString(PreferenceConsts.event_title_key, null);
        String time = preferences.getString(PreferenceConsts.event_time_key, null);
        if (title == null || time == null) {
            title = "";
            time = "";
        }
        eventTitleTextView.setText(title);
        eventTimeTextView.setText(time);
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(time)) {
            eventTitleTextView.setVisibility(View.GONE);
            eventTimeTextView.setVisibility(View.GONE);
        } else {
            eventTimeTextView.setVisibility(View.VISIBLE);
            eventTitleTextView.setVisibility(View.VISIBLE);
        }
        eventTitleTextView.setTextSize(Integer.parseInt(preferences.getString(PreferenceConsts.title_font_key, PreferenceConsts.title_font_default)));
        eventTimeTextView.setTextSize(Integer.parseInt(preferences.getString(PreferenceConsts.time_font_key, PreferenceConsts.time_font_default)));
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui")) {
            return;
        }
        XposedHelpers.findAndHookMethod("com.android.keyguard.KeyguardStatusView", lpparam.classLoader, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log("inject views");
                GridLayout self = (GridLayout) param.thisObject;
                Context context = self.getContext();
                LayoutInflater layoutInflater = LayoutInflater.from(self.getContext());
                View view = layoutInflater.inflate(moduleRes.getLayout(moduleRes.getIdentifier("lockscreen", "layout", PACKAGE_NAME)), null);
                self.addView(view);
                eventTitleTextView = (TextView) view.findViewById(moduleRes.getIdentifier("event_title", "id", PACKAGE_NAME));
                eventTimeTextView = (TextView) view.findViewById(moduleRes.getIdentifier("event_time", "id", PACKAGE_NAME));
                XposedBridge.log("finished injecting");
                preferences = new RemotePreferences(context, PreferenceConsts.authority, PreferenceConsts.preferences);
                preferences.registerOnSharedPreferenceChangeListener(LockscreenHook.this);
                XposedBridge.log("registered with prefs");
                refreshShownEvents();
            }
        });
        XposedBridge.log("Lockscreen calender Xposed module initialized!");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        XposedBridge.log("preferences changed");
        refreshShownEvents();
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        moduleRes = XModuleResources.createInstance(sModulePath, resparam.res);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        sModulePath = startupParam.modulePath;
    }
}