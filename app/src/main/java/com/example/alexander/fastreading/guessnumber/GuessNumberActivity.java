package com.example.alexander.fastreading.guessnumber;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberDescriptionFragment;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberMainFragment;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberPrepareFragment;


public class GuessNumberActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_number_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getString(R.string.guess_number));
        setSupportActionBar(toolbar);

        fragmentManager = getFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SettingsManager.isGuessNumberShowHelp()) {
            startDescriptionFragment();
        } else {
            startPrepareFragment();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guess_number_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart: {
                startPrepareFragment();
                return true;
            }
            case R.id.info: {
                Intent intent = new Intent(this, GuessNumberDescriptionActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void startTrainingFragment() {
        fragmentManager.beginTransaction().
                replace(R.id.guess_number_fragment_container, new GuessNumberMainFragment()).
                commit();
    }

    public void startDescriptionFragment() {
        fragmentManager.
                beginTransaction().
                replace(R.id.guess_number_fragment_container, new GuessNumberDescriptionFragment()).
                commit();
    }

    public void startPrepareFragment() {
        fragmentManager.
                beginTransaction().
                replace(R.id.guess_number_fragment_container, new GuessNumberPrepareFragment()).
                commit();
    }

}
