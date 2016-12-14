package com.gobbledygook.theawless.eventlock.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.events.EventsGismo;
import com.gobbledygook.theawless.eventlock.helper.Constants;
import com.gobbledygook.theawless.eventlock.helper.XposedUtils;
import com.gobbledygook.theawless.eventlock.receivers.UpdateReceiver;
import com.gobbledygook.theawless.eventlock.services.CalendarLoaderService;

public class MainActivity extends AppCompatActivity {
    private boolean previewOn = false;
    private EventsGismo eventGismo = null;
    private UpdateReceiver updateReceiver = new UpdateReceiver() {
        @Override
        protected EventsGismo getGismo(String action) {
            return eventGismo;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (savedInstanceState == null) {
            if (!XposedUtils.isModuleEnabled()) {
                showEnableModuleDialog();
            } else if (XposedUtils.isModuleUpdated()) {
                showModuleUpdatedDialog();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.events_update);
        intentFilter.addAction(Constants.current_event_update);
        intentFilter.addAction(Constants.looks_update);
        registerReceiver(updateReceiver, intentFilter);
    }

    protected void onDestroy() {
        unregisterReceiver(updateReceiver);
        handleRefresh();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview: {
                handlePreview(item);
                return true;
            }
            case R.id.action_refresh: {
                handleRefresh();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void handlePreview(MenuItem item) {
        if (eventGismo == null) {
            eventGismo = new EventsGismo(PreferenceManager.getDefaultSharedPreferences(this));
            ((GridLayout) findViewById(R.id.events_placeholder)).addView(eventGismo.getRecyclerView(this));
            startService(new Intent(this, CalendarLoaderService.class));
        }
        if (!previewOn) {
            findViewById(R.id.events_placeholder).setVisibility(View.VISIBLE);
            item.setIcon(getDrawable(R.drawable.action_preview_off_icon));
        } else {
            findViewById(R.id.events_placeholder).setVisibility(View.GONE);
            item.setIcon(getDrawable(R.drawable.action_preview_on_icon));
        }
        previewOn = !previewOn;
    }

    private void handleRefresh() {
        sendBroadcast(new Intent(Constants.looks_update));
        startService(new Intent(this, CalendarLoaderService.class));
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
                .setPositiveButton(R.string.clear_data, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .addCategory(Intent.CATEGORY_DEFAULT)
                                .setData(Uri.parse("package:" + getPackageName()))
                        );
                    }
                })
                .setNeutralButton(R.string.reboot, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startXposedActivity(XposedUtils.XPOSED_SECTION_INSTALL);
                    }
                })
                .setNegativeButton(R.string.ignore, null)
                .show();
    }
}
