package com.example.alexander.fastreading.reader.reader.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.alexander.fastreading.R;

/**
 * Created by Alexander on 25.09.2016.
 */
public class ReaderSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.reader_settings);
    }
}
