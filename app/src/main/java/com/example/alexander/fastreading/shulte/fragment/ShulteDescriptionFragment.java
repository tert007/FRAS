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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.ViewOnClickListener;
import com.example.alexander.fastreading.shulte.ShulteActivity;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteDescriptionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shulte_description_fragment, null);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.shulte_description_dont_show_again_check_box);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsManager.setShulteShowHelp(! checkBox.isChecked());
            }
        });

        final Button button = (Button) view.findViewById(R.id.shulte_description_exit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShulteActivity) getActivity()).startTrainingFragment();
            }
        });

        return view;
    }
}
