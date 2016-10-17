package com.gobbledygook.theawless.eventlock.xposed;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.gobbledygook.theawless.eventlock.BuildConfig;

public final class XposedUtils {
    public static final String XPOSED_SECTION_MODULES = "modules";
    public static final String XPOSED_SECTION_INSTALL = "install";
    private static final int MODULE_VERSION = BuildConfig.MODULE_VERSION;
    private static final String XPOSED_PACKAGE = "de.robv.android.xposed.installer";
    private static final String XPOSED_ACTION = XPOSED_PACKAGE + ".OPEN_SECTION";
    private static final String XPOSED_EXTRA_SECTION = "section";

    public static boolean isModuleEnabled() {
        return getModuleVersion() >= 0;
    }

    public static boolean isModuleUpdated() {
        return MODULE_VERSION != getModuleVersion();
    }

    private static int getModuleVersion() {
        return -1;
    }

    public static boolean startXposedActivity(Context context, String section) {
        Intent intent = new Intent(XPOSED_ACTION);
        intent.putExtra(XPOSED_EXTRA_SECTION, section);
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
