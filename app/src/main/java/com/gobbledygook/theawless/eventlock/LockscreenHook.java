package com.gobbledygook.theawless.eventlock;

import android.content.res.XModuleResources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LockscreenHook implements IXposedHookZygoteInit, IXposedHookInitPackageResources, IXposedHookLoadPackage {
    private static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    private static String sModulePath;
    private TextView eventTitleTextView;
    private TextView eventTimeTextView;
    private XModuleResources moduleRes;

    @SuppressWarnings("MissingPermission")
    private void refreshEvents() {
        XposedBridge.log("refresh");
        eventTitleTextView.setText(eventTitleTextView.getText() + ".");
        eventTimeTextView.setText(eventTimeTextView.getText() + ".");
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
                LayoutInflater layoutInflater = LayoutInflater.from(self.getContext());
                XmlPullParser parser;
                parser = moduleRes.getLayout(moduleRes.getIdentifier("lockscreen", "layout", PACKAGE_NAME));
                View view = layoutInflater.inflate(parser, null);
                self.addView(view);
                eventTitleTextView = (TextView) view.findViewById(moduleRes.getIdentifier("event_title", "id", PACKAGE_NAME));
                eventTimeTextView = (TextView) view.findViewById(moduleRes.getIdentifier("event_time", "id", PACKAGE_NAME));
                XposedBridge.log("finished injecting");
                refreshEvents();
            }
        });
        XposedBridge.log("Lockscreen calender Xposed module initialized!");
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