package com.example.alexander.fastreading.visionfield;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.visionfield.fragment.VisionFieldDescriptionFragment;
import com.example.alexander.fastreading.visionfield.fragment.VisionFieldMainFragment;

public class VisionFieldActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vision_field_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.vision_field);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        SettingsManager.setVisionFieldShowHelp(true);
        fragmentManager = getFragmentManager();
    }

    public void startDescriptionFragment() {
        fragmentManager.
                beginTransaction().
                replace(R.id.vision_field_fragment_container, new VisionFieldDescriptionFragment()).
                commit();
    }

    public void startTrainingFragment() {
        fragmentManager.
                beginTransaction().
                replace(R.id.vision_field_fragment_container, new VisionFieldMainFragment()).
                commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SettingsManager.isVisionFieldShowHelp()) {
            startDescriptionFragment();
        } else {
            startTrainingFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:
                startTrainingFragment();
                return true;
            case R.id.help: {
                Intent intent = new Intent(this, VisionFieldDescriptionActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.settings: {
                Intent intent = new Intent(this, VisionFieldSettingsActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vision_field_toolbar, menu);
        return true;
    }
}