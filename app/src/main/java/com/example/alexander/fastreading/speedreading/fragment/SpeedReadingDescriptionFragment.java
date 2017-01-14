package com.example.alexander.fastreading.speedreading.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.speedreading.SpeedReadingActivity;

/**
 * Created by Alexander on 27.07.2016.
 */
public class SpeedReadingDescriptionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speed_reading_description_fragment, container, false);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.speed_reading_description_dont_show_again_check_box);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsManager.setSpeedReadingShowHelp(! checkBox.isChecked());
            }
        });

        final Button button = (Button) view.findViewById(R.id.speed_reading_description_start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SpeedReadingActivity) getActivity()).startPrepareFragment();
            }
        });

        return view;
    }

}
