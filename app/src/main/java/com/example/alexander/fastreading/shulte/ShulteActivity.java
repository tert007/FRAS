package com.example.alexander.fastreading.shulte;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.shulte.fragment.ShulteDescriptionFragment;
import com.example.alexander.fastreading.shulte.fragment.ShulteMainFragment;
import com.example.alexander.fastreading.shulte.fragment.ShultePrepareFragment;

public class ShulteActivity extends AppCompatActivity {

    //private AdView adView;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shulte_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.shulte_table);
        setSupportActionBar(toolbar);

        //MobileAds.initialize(this, "ca-app-pub-1214906094509332~8123538200");

        //adView = (AdView) findViewById(R.id.adView);
        /*
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                adView.setVisibility(View.GONE);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
*/
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
                startPrepareFragment();
                return true;
            case R.id.info: {
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
/*
        if (adView != null) {
            adView.resume();
        }
*/
        if (SettingsManager.isShulteShowHelp()) {
            startDescriptionFragment();
        } else {
            startPrepareFragment();
        }
    }

    @Override
    protected void onPause() {/*
        if (adView != null) {
            adView.pause();
        }*/
        super.onPause();
    }


    @Override
    public void onDestroy() {/*
        if (adView != null) {
            adView.destroy();
        }*/
        super.onDestroy();
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

    public void startPrepareFragment() {
        fragmentManager.beginTransaction().
                replace(R.id.shulte_fragment_container, new ShultePrepareFragment()).
                commit();
    }
}
