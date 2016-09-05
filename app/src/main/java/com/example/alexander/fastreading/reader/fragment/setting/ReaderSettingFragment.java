package com.example.alexander.fastreading.reader.fragment.setting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;

/**
 * Created by Alexander on 01.09.2016.
 */
public class ReaderSettingFragment extends Fragment {

    Spinner textSizeSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_settings_fragment, container, false);

        textSizeSpinner = (Spinner)view.findViewById(R.id.reader_settings_text_size_spinner);

        String[] spinnerItems = getResources().getStringArray(R.array.text_size_settings);
        String complexity = String.valueOf(SettingsManager.getReaderTextSize());
        int itemPosition = 0;

        for (int i = 0; i < spinnerItems.length; i++){
            if (spinnerItems[i].equals(complexity)){
                itemPosition = i;
                break;
            }
        }
        textSizeSpinner.setSelection(itemPosition);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        int textSize = Integer.valueOf((String)textSizeSpinner.getSelectedItem());
        SettingsManager.setReaderTextSize(textSize);
    }

}
