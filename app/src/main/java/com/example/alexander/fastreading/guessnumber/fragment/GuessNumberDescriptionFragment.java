package com.example.alexander.fastreading.guessnumber.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.guessnumber.GuessNumberActivity;
import com.example.alexander.fastreading.shulte.ShulteActivity;

/**
 * Created by Alexander on 27.07.2016.
 */
public class GuessNumberDescriptionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_number_description_fragment, null);

        boolean showCheckBox = getArguments().getBoolean("show_check_box");

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.guess_number_description_dont_show_again_check_box);
        if (! showCheckBox) {
            checkBox.setVisibility(View.INVISIBLE);
        } else {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SettingsManager.setVisionFieldShowHelp(! checkBox.isChecked());
                }
            });
        }

        final Button button = (Button) view.findViewById(R.id.guess_number_description_exit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GuessNumberActivity) getActivity()).startTrainingFragment();
            }
        });

        return view;
    }
}
