package com.example.alexander.fastreading.shulte;

import android.app.FragmentManager;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.ViewOnClickListener;
import com.example.alexander.fastreading.shulte.fragment.ShulteMainFragment;
import com.example.alexander.fastreading.shulte.fragment.ShulteSettingsFragment;

public class ShulteActivity extends AppCompatActivity implements ViewOnClickListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shulte_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.schulte_table);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        fragmentManager = getFragmentManager();

        startSettingsFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings :
                startSettingsFragment();
                return true;
            case R.id.restart:
                startTrainingFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shulte_toolbar, menu);
        return true;
    }

    @Override
    public void viewOnClick(View v) {
        switch (v.getId()){
            case R.id.shulte_start_training_button:
                startTrainingFragment();
                break;
        }
    }

    public void startTrainingFragment() {
        ShulteMainFragment mainFragment = new ShulteMainFragment();

        fragmentManager.beginTransaction().
                replace(R.id.shulte_fragment_container, mainFragment).
                commit();
    }

    public void startSettingsFragment() {
        ShulteSettingsFragment settingsFragment = new ShulteSettingsFragment();
        settingsFragment.delegate = this;

        fragmentManager.beginTransaction().
                replace(R.id.shulte_fragment_container, settingsFragment).
                commit();
    }

}
