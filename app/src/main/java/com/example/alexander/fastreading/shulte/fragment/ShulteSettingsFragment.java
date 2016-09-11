package com.example.alexander.fastreading.shulte.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.shulte.ViewOnClickListener;
import com.example.alexander.fastreading.shulte.ShulteTableType;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteSettingsFragment extends Fragment {

    public ViewOnClickListener delegate;

    private Button startTrainingButton;
    private SwitchCompat switchCompat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shulte_settings_fragment, null);

        switchCompat = (SwitchCompat) view.findViewById(R.id.shulte_table_type_switch);
        if (SettingsManager.getShulteTableType().equals(ShulteTableType.LETTERS)){
            switchCompat.setChecked(true);
        }

        startTrainingButton = (Button) view.findViewById(R.id.shulte_start_training_button);
        startTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.viewOnClick(v);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(switchCompat.isChecked()){
            SettingsManager.setShulteTableType(ShulteTableType.LETTERS);
        } else {
            SettingsManager.setShulteTableType(ShulteTableType.NUMBERS);
        }
    }
}
