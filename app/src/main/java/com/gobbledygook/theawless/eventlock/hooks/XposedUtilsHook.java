package com.gobbledygook.theawless.eventlock.hooks;

import com.gobbledygook.theawless.eventlock.BuildConfig;
import com.gobbledygook.theawless.eventlock.helper.XposedUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedUtilsHook implements IXposedHookLoadPackage {
    private static final String EVENTLOCK_PACKAGE = BuildConfig.APPLICATION_ID;
    private static final int MODULE_VERSION = BuildConfig.VERSION_CODE;

    private static void hookXposedUtils(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(XposedUtils.class.getName(), lpparam.classLoader, "getModuleVersion", XC_MethodReplacement.returnConstant(MODULE_VERSION));
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (EVENTLOCK_PACKAGE.equals(lpparam.packageName)) {
            hookXposedUtils(lpparam);
        }
    }
}
