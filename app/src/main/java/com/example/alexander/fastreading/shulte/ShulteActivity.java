package com.example.alexander.fastreading.shulte;

import android.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.shulte.fragment.ShulteDescriptionFragment;
import com.example.alexander.fastreading.shulte.fragment.ShulteMainFragment;

public class ShulteActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shulte_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.shulte_table);
        setSupportActionBar(toolbar);

        fragmentManager = getFragmentManager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings : {
                Intent intent = new Intent(this, ShulteSettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.restart:
                startTrainingFragment();
                return true;
            case R.id.help: {
                Intent intent = new Intent(this, ShulteDescriptionActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SettingsManager.isShulteShowHelp()) {
            startDescriptionFragment();
        } else {
            startTrainingFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shulte_toolbar, menu);
        return true;
    }

    public void startDescriptionFragment() {
        fragmentManager.beginTransaction().
                replace(R.id.shulte_fragment_container, new ShulteDescriptionFragment()).
                commit();
    }

    public void startTrainingFragment() {
        fragmentManager.beginTransaction().
                replace(R.id.shulte_fragment_container, new ShulteMainFragment()).
                commit();
    }
}
