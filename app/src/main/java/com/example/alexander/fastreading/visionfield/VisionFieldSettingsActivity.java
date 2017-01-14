package com.example.alexander.fastreading.visionfield;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;

/**
 * Created by Alexander on 29.10.2016.
 */
public class VisionFieldSettingsActivity extends AppCompatActivity {

    private static final int[] COMPLEXITY = new int[] {30_000, 60_000, 120_000, 180_000, 240_000, 300_000};
    private static final int[] SHOW_TIME = new int[] {1000, 800, 600};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vision_field_settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View complexityView = findViewById(R.id.vision_field_settings_complexity_view);
        final Spinner complexitySpinner = (Spinner) complexityView.findViewById(R.id.vision_field_settings_complexity_spinner);

        complexityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complexitySpinner.performClick();
            }
        });

        int complexity = SettingsManager.getVisionFieldComplexity();
        for (int i = 0; i < COMPLEXITY.length; i++) {
            if (complexity == COMPLEXITY[i]) {
                complexitySpinner.setSelection(i);
            }
        }

        complexitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SettingsManager.setVisionFieldComplexity(COMPLEXITY[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final View showTimeView = findViewById(R.id.vision_field_settings_show_time_view);
        final Spinner showTimeSpinner = (Spinner) showTimeView.findViewById(R.id.vision_field_settings_show_time_spinner);

        showTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeSpinner.performClick();
            }
        });

        int showTime = SettingsManager.getVisionFieldShowDelay();
        for (int i = 0; i < SHOW_TIME.length; i++) {
            if (showTime == SHOW_TIME[i]) {
                showTimeSpinner.setSelection(i);
            }
        }

        showTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SettingsManager.setVisionFieldShowDelay(SHOW_TIME[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
