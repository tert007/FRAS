package com.example.alexander.fastreading.speedreading;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.Training;
import com.example.alexander.fastreading.speedreading.fragment.SpeedReadingDescriptionFragment;
import com.example.alexander.fastreading.speedreading.fragment.SpeedReadingTrainingFragment;
import com.example.alexander.fastreading.speedreading.fragment.SpeedReadingAnswersFragment;
import com.example.alexander.fastreading.speedreading.fragment.SpeedReadingPrepareFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class SpeedReadingActivity extends AppCompatActivity implements SpeedReadingLastWordResponse {

    private AdView adView;
    private boolean isPremiumUser;

    private FragmentManager fragmentManager;

    private enum FragmentState { Prepare, Training, Description, Answer};
    private FragmentState currentState;

    public int countAnswer = 0;
    public final int[] speeds = new int[] { 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000 };

    public int index = 2;
    public int speed = 60_000 / speeds[index];

    public void updateSpeed(boolean increment){
        if(increment){
            if(index < speeds.length - 1) {
                index++;
                speed = 60_000 / speeds[index];
            }
        } else {
            if(index > 0) {
                index--;
                speed = 60_000 / speeds[index];
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_reading_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.speed_reading);
        toolbar.setTitleTextColor(Color.WHITE);
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

        if (SettingsManager.isSpeedReadingShowHelp()) {
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
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:

                this.countAnswer = 0;
                this.index = 2;
                this.speed = speeds[index];

                startPrepareFragment();
                return true;
            case R.id.info: {
                Intent intent = new Intent(this, SpeedReadingDescriptionActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.speed_reading_toolbar, menu);
        return true;
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
            case Answer:
                ((Training) currentFragment).trainingOnBackPressed();
                break;
        }
    }

    @Override
    public void onLastWordResponse(String trueAnswer) {
        startAnswerFragment(trueAnswer);
    }

    public void startTrainingFragment(){
        currentState = FragmentState.Training;

        SpeedReadingTrainingFragment mainFragment = new SpeedReadingTrainingFragment();
        mainFragment.lastWordDelegate = this;

        currentFragment = mainFragment;

        fragmentManager.beginTransaction().
                replace(R.id.speed_reading_fragment_container, mainFragment).
                commit();
    }

    public void startPrepareFragment(){
        currentState = FragmentState.Prepare;

        fragmentManager.beginTransaction().
                replace(R.id.speed_reading_fragment_container, new SpeedReadingPrepareFragment()).
                commit();
    }

    public void startAnswerFragment(String trueAnswer){
        currentState = FragmentState.Answer;

        SpeedReadingAnswersFragment answersFragment = new SpeedReadingAnswersFragment();

        Bundle bundle = new Bundle();
        bundle.putString("true_answer", trueAnswer);

        answersFragment.setArguments(bundle);

        currentFragment = answersFragment;

        fragmentManager.beginTransaction().
                replace(R.id.speed_reading_fragment_container, answersFragment).
                commit();
    }

    private void startDescriptionFragment() {
        currentState = FragmentState.Description;

        fragmentManager.beginTransaction().
                replace(R.id.speed_reading_fragment_container, new SpeedReadingDescriptionFragment()).
                commit();
    }
}
