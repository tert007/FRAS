package com.example.alexander.fastreading.guessnumber.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.ViewOnClickListener;
import com.example.alexander.fastreading.SettingsManager;

/**
 * Created by Alexander on 31.07.2016.
 */
public class GuessNumberSettingsFragment extends Fragment {

    public ViewOnClickListener delegate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_number_settings_fragment, null);

        final Spinner spinner = (Spinner) view.findViewById(R.id.guess_number_settings_spinner);

        String[] spinnerItems = getResources().getStringArray(R.array.guess_number_settings);
        String complexity = String.valueOf(SettingsManager.getGuessNumberComplexity());
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
                SettingsManager.setGuessNumberComplexity(Integer.valueOf((String)spinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }
}
