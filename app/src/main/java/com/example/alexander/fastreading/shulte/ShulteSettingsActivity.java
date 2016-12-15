package com.example.alexander.fastreading.shulte;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;

public class ShulteSettingsActivity extends AppCompatActivity {

    private static final int DEFAULT_COMPLEXITY = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shulte_settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View complexityView = findViewById(R.id.shulte_settings_complexity_view);
        final Spinner complexitySpinner = (Spinner) complexityView.findViewById(R.id.shulte_settings_spinner);
        final TextView complexityLockTextView = (TextView) findViewById(R.id.shulte_settings_complexity_lock_text_view);

        complexityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complexitySpinner.performClick();
            }
        });

        String[] spinnerItems = getResources().getStringArray(R.array.shulte_settings);
        String complexity = String.valueOf(SettingsManager.getShulteComplexity());
        int itemPosition = 0;

        for (int i = 0; i < spinnerItems.length; i++){
            if (spinnerItems[i].equals(complexity)){
                itemPosition = i;
                break;
            }
        }
        complexitySpinner.setSelection(itemPosition);

        complexitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int complexity = Integer.valueOf((String) complexitySpinner.getSelectedItem());
                SettingsManager.setShulteComplexity(complexity);

                if (complexity == DEFAULT_COMPLEXITY) {
                    complexityLockTextView.setVisibility(View.GONE);
                } else {
                    complexityLockTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final View permutationView = findViewById(R.id.shulte_settings_permutation_view);

        final SwitchCompat permutationSwitch = (SwitchCompat) permutationView.findViewById(R.id.shulte_settings_permutation_switch);
        permutationSwitch.setChecked(SettingsManager.isShultePermutation());

        permutationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permutationSwitch.performClick();
            }
        });

        permutationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsManager.setShultePermutation(permutationSwitch.isChecked());
            }
        });

        final View coloredView = findViewById(R.id.shulte_settings_colored_view);
        final SwitchCompat coloredSwitch = (SwitchCompat) coloredView.findViewById(R.id.shulte_settings_colored_switch);
        coloredSwitch.setChecked(SettingsManager.isShulteColored());

        coloredView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coloredSwitch.performClick();
            }
        });

        coloredSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsManager.setShulteColored(coloredSwitch.isChecked());
            }
        });

        final View eyeModeView = findViewById(R.id.shulte_settings_eye_mode_view);

        final SwitchCompat eyeModeSwitch = (SwitchCompat) eyeModeView.findViewById(R.id.shulte_settings_eye_mode_switch);
        boolean eyeMode = SettingsManager.isShulteEyeMode();
        eyeModeSwitch.setChecked(eyeMode);

        final TextView eyesModeLockTextView = (TextView) findViewById(R.id.shulte_settings_eyes_mode_lock_text_view);
        final TextView eyesModeHowToUSeTextView = (TextView) findViewById(R.id.shulte_settings_eyes_mode_how_to_use_text_view);

        if (eyeMode) {
            eyesModeLockTextView.setVisibility(View.VISIBLE);
            eyesModeHowToUSeTextView.setVisibility(View.VISIBLE);
            permutationView.setEnabled(false);
            permutationSwitch.setEnabled(false);
        } else {
            permutationView.setEnabled(true);
            permutationSwitch.setEnabled(true);
        }

        eyeModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eyeModeSwitch.performClick();
            }
        });
        eyeModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean eyeMode = eyeModeSwitch.isChecked();
                SettingsManager.setShulteEyeMode(eyeMode);

                if  (eyeMode) {
                    eyesModeLockTextView.setVisibility(View.VISIBLE);
                    eyesModeHowToUSeTextView.setVisibility(View.VISIBLE);

                    permutationSwitch.setChecked(false);
                    permutationView.setEnabled(false);
                    permutationSwitch.setEnabled(false);

                    SettingsManager.setShultePermutation(false);
                } else {
                    eyesModeLockTextView.setVisibility(View.GONE);
                    eyesModeHowToUSeTextView.setVisibility(View.GONE);

                    permutationView.setEnabled(true);
                    permutationSwitch.setEnabled(true);
                }
            }
        });

    }
}
