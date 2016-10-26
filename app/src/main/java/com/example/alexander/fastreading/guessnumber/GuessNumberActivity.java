package com.example.alexander.fastreading.guessnumber;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.ViewOnClickListener;
import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberDescriptionFragment;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberMainFragment;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberSettingsFragment;
import com.example.alexander.fastreading.visionfield.fragment.VisionFieldDescriptionFragment;


public class GuessNumberActivity extends AppCompatActivity  implements ViewOnClickListener {

    //GuessNumberSettingsFragment settingsFragment;
    //GuessNumberMainFragment mainFragment;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_number_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getString(R.string.guess_number));
        setSupportActionBar(toolbar);

        //settingsFragment = new GuessNumberSettingsFragment();
        //settingsFragment.delegate = this;

        fragmentManager = getFragmentManager();

        if (SettingsManager.isGuessNumberShowHelp()) {
            startDescriptionFragment(true);
        } else {
            startTrainingFragment();
        }
    }

    public void startTrainingFragment() {
        fragmentManager.beginTransaction().
                replace(R.id.guess_number_fragment_container, new GuessNumberMainFragment()).
                commit();
    }

    public void startDescriptionFragment(boolean showCheckBox) {
        GuessNumberDescriptionFragment descriptionFragment = new GuessNumberDescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("show_check_box", showCheckBox);

        descriptionFragment.setArguments(bundle);

        fragmentManager.
                beginTransaction().
                replace(R.id.guess_number_fragment_container, descriptionFragment).
                commit();
    }

    public void startSettingsFragment(){
        //mainFragment = new GuessNumberMainFragment();
        //fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.guess_number_fragment_container, settingsFragment);
        //fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guess_number_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:
                startTrainingFragment();
                return true;
            case R.id.help:
                startDescriptionFragment(false);
                return true;
            case R.id.settings :

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void viewOnClick(View v) {
        //mainFragment = new GuessNumberMainFragment();
        //fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.guess_number_fragment_container, mainFragment);
        //fragmentTransaction.commit();
    }
}
