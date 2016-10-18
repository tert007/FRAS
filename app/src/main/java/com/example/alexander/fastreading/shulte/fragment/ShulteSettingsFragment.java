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

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.shulte.ViewOnClickListener;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteSettingsFragment extends Fragment {

    public ViewOnClickListener delegate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shulte_settings_fragment, null);

        final Spinner spinner = (Spinner) view.findViewById(R.id.shulte_settings_spinner);

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
                SettingsManager.setShulteComplexity(Integer.valueOf((String) spinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Button startTrainingButton = (Button) view.findViewById(R.id.shulte_start_training_button);
        startTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.viewOnClick(v);
            }
        });

        return view;
    }
}
