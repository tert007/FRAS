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

import com.example.alexander.fastreading.ViewOnClickListener;
import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberMainFragment;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberSettingsFragment;


public class GuessNumberActivity extends AppCompatActivity  implements ViewOnClickListener {

    GuessNumberSettingsFragment settingsFragment;
    GuessNumberMainFragment mainFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_number_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setTitle(getString(R.string.remember_number));
        setSupportActionBar(toolbar);

        settingsFragment = new GuessNumberSettingsFragment();
        settingsFragment.delegate = this;

        fragmentManager = getFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.guess_number_fragment_container, settingsFragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guess_number_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guess_number_toolbar_settings :
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.guess_number_fragment_container, settingsFragment);
                fragmentTransaction.commit();

                //getSupportActionBar().setTitle(R.string.settings);
                return true;
            case R.id.guess_number_toolbar_restart:
                startTrainingFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }    }

    public void startSettingsFragment(){
        mainFragment = new GuessNumberMainFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.guess_number_fragment_container, settingsFragment);
        fragmentTransaction.commit();
    }

    public void startTrainingFragment(){
        mainFragment = new GuessNumberMainFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.guess_number_fragment_container, mainFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void viewOnClick(View v) {
        mainFragment = new GuessNumberMainFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.guess_number_fragment_container, mainFragment);
        fragmentTransaction.commit();
    }
}
