package com.example.alexander.fastreading.visionfield.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.visionfield.VisionFieldActivity;

/**
 * Created by Alexander on 27.07.2016.
 */
public class VisionFieldSettingsFragment extends Fragment {

    private static final int[] COMPLEXITY = new int[] {30, 60, 120, 180, 240, 300};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vision_field_settings_fragment, container, false);

        final View complexityView = view.findViewById(R.id.vision_field_settings_complexity_view);
        final Spinner spinner = (Spinner) complexityView.findViewById(R.id.vision_field_settings_spinner);

        complexityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        int complexity = SettingsManager.getVisionFieldComplexity();
        for (int i = 0; i < COMPLEXITY.length; i++) {
            if (complexity == COMPLEXITY[i]) {
                spinner.setSelection(i);
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int itemPosition = spinner.getSelectedItemPosition();
                SettingsManager.setVisionFieldComplexity(COMPLEXITY[itemPosition]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

}
