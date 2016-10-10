package com.gobbledygook.theawless.eventlock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        if (savedInstanceState == null) {
            if (!XposedUtils.isModuleEnabled()) {
                showEnableModuleDialog();
            } else if (XposedUtils.isModuleUpdated()) {
                showModuleUpdatedDialog();
            }
        }
    }

    private void startXposedActivity(String section) {
        if (!XposedUtils.startXposedActivity(this, section)) {
            Toast.makeText(this, R.string.xposed_not_installed, Toast.LENGTH_SHORT).show();
            startBrowserActivity(getString(R.string.xposed_forum_url));
        }
    }

    private void startBrowserActivity(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void showEnableModuleDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.enable_xposed_module_title)
                .setMessage(R.string.enable_xposed_module_message)
                .setIcon(R.drawable.warning_icon)
                .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startXposedActivity(XposedUtils.XPOSED_SECTION_MODULES);
                    }
                })
                .setNeutralButton(R.string.report_bug, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startBrowserActivity(getString(R.string.github_issues_url));
                    }
                })
                .setNegativeButton(R.string.ignore, null)
                .show();
    }

    private void showModuleUpdatedDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.module_outdated_title)
                .setMessage(R.string.module_outdated_message)
                .setIcon(R.drawable.warning_icon)
                .setPositiveButton(R.string.reboot, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startXposedActivity(XposedUtils.XPOSED_SECTION_INSTALL);
                    }
                })
                .setNegativeButton(R.string.ignore, null)
                .show();
    }
}
