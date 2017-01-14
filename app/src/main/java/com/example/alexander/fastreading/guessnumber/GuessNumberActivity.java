package com.example.alexander.fastreading.guessnumber;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.Training;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberDescriptionFragment;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberTrainingFragment;
import com.example.alexander.fastreading.guessnumber.fragment.GuessNumberPrepareFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class GuessNumberActivity extends AppCompatActivity {

    private AdView adView;
    private boolean isPremiumUser;

    FragmentManager fragmentManager;

    private enum FragmentState { Prepare, Training, Description };
    private FragmentState currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_number_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getString(R.string.guess_number));
        setSupportActionBar(toolbar);

        adView = (AdView) findViewById(R.id.adView);

        isPremiumUser = SettingsManager.isPremiumUser();
        if (isPremiumUser) {
            adView.setVisibility(View.GONE);
        } else {
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
        }

        fragmentManager = getFragmentManager();

        if (SettingsManager.isGuessNumberShowHelp()) {
            startDescriptionFragment();
        } else {
            startPrepareFragment();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
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

    private Fragment currentFragment;

    @Override
    public void onBackPressed() {
        switch (currentState) {
            case Prepare:
                super.onBackPressed();
                break;
            case Description:
                super.onBackPressed();
                break;
            case Training:
                ((Training) currentFragment).trainingOnBackPressed();
                break;
        }
    }

    public void startTrainingFragment() {

        currentState = FragmentState.Training;
        currentFragment = new GuessNumberTrainingFragment();

        fragmentManager.beginTransaction().
                replace(R.id.guess_number_fragment_container, currentFragment).
                commit();
    }

    public void startDescriptionFragment() {

        currentState = FragmentState.Description;

        fragmentManager.
                beginTransaction().
                replace(R.id.guess_number_fragment_container, new GuessNumberDescriptionFragment()).
                commit();
    }

    public void startPrepareFragment() {

        currentState = FragmentState.Prepare;

        fragmentManager.
                beginTransaction().
                replace(R.id.guess_number_fragment_container, new GuessNumberPrepareFragment()).
                commit();
    }

}
