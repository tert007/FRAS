package com.example.alexander.fastreading.shulte;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.shulte.fragment.ShulteMainFragment;
import com.example.alexander.fastreading.shulte.fragment.ShulteSettingsFragment;

public class ShulteActivity extends AppCompatActivity implements ViewOnClickListener {

    ShulteSettingsFragment settingsFragment;
    ShulteMainFragment gridFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shulte_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);

        settingsFragment = new ShulteSettingsFragment();
        settingsFragment.delegate = this;

        fragmentManager = getFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.shulte_fragment_container, settingsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings :
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.shulte_fragment_container, settingsFragment);
                fragmentTransaction.commit();

                getSupportActionBar().setTitle(R.string.settings);
                return true;
            case R.id.restart:
                gridFragment = new ShulteMainFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("count_row", 5);

                gridFragment.setArguments(bundle);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.shulte_fragment_container, gridFragment);
                fragmentTransaction.commit();
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
                gridFragment = new ShulteMainFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("count_row", 5);

                gridFragment.setArguments(bundle);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.shulte_fragment_container, gridFragment);
                fragmentTransaction.commit();

                break;
        }
    }
}
