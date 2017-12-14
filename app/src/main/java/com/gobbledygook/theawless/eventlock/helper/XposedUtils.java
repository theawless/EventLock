package com.gobbledygook.theawless.eventlock.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.gobbledygook.theawless.eventlock.BuildConfig;

public class XposedUtils {
    private static final String XPOSED_PACKAGE = "de.robv.android.xposed.installer";
    private static final int MODULE_VERSION = BuildConfig.VERSION_CODE;

    public static boolean isModuleEnabled() {
        return getModuleVersion() >= 0;
    }

    public static boolean isModuleUpdated() {
        return MODULE_VERSION != getModuleVersion();
    }

    private static int getModuleVersion() {
        return -1;
    }

    public static boolean startXposedActivity(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(XPOSED_PACKAGE);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
