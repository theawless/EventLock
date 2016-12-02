package com.gobbledygook.theawless.eventlock.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gobbledygook.theawless.eventlock.R;
import com.gobbledygook.theawless.eventlock.api.CalendarLoaderAndFetcher;
import com.gobbledygook.theawless.eventlock.api.Event;
import com.gobbledygook.theawless.eventlock.api.EventsAdapter;
import com.gobbledygook.theawless.eventlock.background.CalendarLoaderService;
import com.gobbledygook.theawless.eventlock.xposed.XposedUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Boolean previewOn = false;
    private ArrayList<Event> events;
    private RecyclerView recyclerView = null;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview: {
                if (!previewOn) {
                    addPreview();
                    item.setIcon(getDrawable(R.drawable.action_preview_off_icon));
                } else {
                    removePreview();
                    item.setIcon(getDrawable(R.drawable.action_preview_on_icon));
                }
                previewOn = !previewOn;
                return true;
            }
            case R.id.action_refresh: {
                fetchNewEvents();
                startService(new Intent(this, CalendarLoaderService.class));
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void removePreview() {
        findViewById(R.id.events_placeholder).setVisibility(View.GONE);
    }

    private void addPreview() {
        fetchNewEvents();
        findViewById(R.id.events_placeholder).setVisibility(View.VISIBLE);
        if (recyclerView == null) {
            recyclerView = new RecyclerView(this);
            EventsAdapter eventsAdapter = new EventsAdapter(events, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main_coordinator_layout);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(eventsAdapter);
            ((FrameLayout) findViewById(R.id.events_placeholder)).addView(recyclerView);
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

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        for (int keyId : Event.EVENT_CHANGING_KEY_IDS) {
            if (key.equals(getString(keyId))) {
                //reload events for lockscreen
                startService(new Intent(this, CalendarLoaderService.class));
                //reload events on preview
                if (previewOn) {
                    fetchNewEvents();
                }
            }
        }
    }

    private void fetchNewEvents() {
        CalendarLoaderAndFetcher calendarloaderAndFetcher = new CalendarLoaderAndFetcher(this);
        if (calendarloaderAndFetcher.fetch()) {
            events = calendarloaderAndFetcher.events;
        }
    }
}
