package com.example.alexander.fastreading.reader.reader.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.alexander.fastreading.R;

/**
 * Created by Alexander on 05.10.2016.
 */
public class ReaderSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_settings_activity);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.reader_settings_fragment_container, new ReaderSettingsFragment()).
                commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(R.string.settings);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case android.R.id.home:
               super.onBackPressed();
               return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
