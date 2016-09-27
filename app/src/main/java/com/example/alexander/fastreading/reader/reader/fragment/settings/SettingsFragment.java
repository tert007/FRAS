package com.example.alexander.fastreading.reader.reader.fragment.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.alexander.fastreading.R;

/**
 * Created by Alexander on 25.09.2016.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.reader_settings);
    }

}
