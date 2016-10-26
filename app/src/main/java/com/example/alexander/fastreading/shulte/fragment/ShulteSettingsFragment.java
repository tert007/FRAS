package com.example.alexander.fastreading.shulte.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.ViewOnClickListener;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteSettingsFragment extends Fragment {

    private static final int DEFAULT_COMPLEXITY = 5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shulte_settings_fragment, null);

        final View complexityView = view.findViewById(R.id.shulte_settings_complexity_view);
        final Spinner spinner = (Spinner) complexityView.findViewById(R.id.shulte_settings_spinner);
        final TextView complexityLockTextView = (TextView) view.findViewById(R.id.shulte_settings_complexity_lock_text_view);

        complexityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
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
        spinner.setSelection(itemPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int complexity = Integer.valueOf((String) spinner.getSelectedItem());
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

        final SwitchCompat permutationSwitch = (SwitchCompat) view.findViewById(R.id.shulte_permutation_switch);
        permutationSwitch.setChecked(SettingsManager.isShultePermutation());

        permutationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsManager.setShultePermutation(permutationSwitch.isChecked());
            }
        });

        final SwitchCompat coloredSwitch = (SwitchCompat) view.findViewById(R.id.shulte_colored_switch);
        coloredSwitch.setChecked(SettingsManager.isShulteColored());

        coloredSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsManager.setShulteColored(coloredSwitch.isChecked());
            }
        });


        final SwitchCompat eyeModeSwitch = (SwitchCompat) view.findViewById(R.id.shulte_eye_mode_switch);
        boolean eyeMode = SettingsManager.isShulteEyeMode();
        eyeModeSwitch.setChecked(eyeMode);

        final TextView eyesModeLockTextView = (TextView) view.findViewById(R.id.shulte_settings_eyes_mode_lock_text_view);

        if (eyeMode) {
            eyesModeLockTextView.setVisibility(View.VISIBLE);
            permutationSwitch.setEnabled(false);
        } else {
            permutationSwitch.setEnabled(true);
        }

        eyeModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean eyeMode = eyeModeSwitch.isChecked();
                SettingsManager.setShulteEyeMode(eyeMode);

                if  (eyeMode) {
                    eyesModeLockTextView.setVisibility(View.VISIBLE);

                    permutationSwitch.setChecked(false);
                    permutationSwitch.setEnabled(false);
                    SettingsManager.setShultePermutation(false);
                } else {
                    eyesModeLockTextView.setVisibility(View.GONE);

                    permutationSwitch.setEnabled(true);
                }
            }
        });

        return view;
    }
}
