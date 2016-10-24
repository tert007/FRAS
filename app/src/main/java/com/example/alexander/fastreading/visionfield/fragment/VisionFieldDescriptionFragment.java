package com.example.alexander.fastreading.visionfield.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.visionfield.VisionFieldActivity;
import com.example.alexander.fastreading.visionfield.VisionFieldGridAdapter;

import java.util.Random;

/**
 * Created by Alexander on 27.07.2016.
 */
public class VisionFieldDescriptionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vision_field_description_fragment, container, false);

        boolean showCheckBox = getArguments().getBoolean("show_check_box");

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.vision_field_description_dont_show_again_check_box);
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

        final Button button = (Button) view.findViewById(R.id.vision_field_description_exit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((VisionFieldActivity) getActivity()).startTrainingFragment();
            }
        });

        return view;
    }

}
