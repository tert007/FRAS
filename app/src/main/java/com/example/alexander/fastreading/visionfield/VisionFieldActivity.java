package com.example.alexander.fastreading.visionfield;

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

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.Training;
import com.example.alexander.fastreading.visionfield.fragment.VisionFieldDescriptionFragment;
import com.example.alexander.fastreading.visionfield.fragment.VisionFieldTrainingFragment;
import com.example.alexander.fastreading.visionfield.fragment.VisionFieldPrepareFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class VisionFieldActivity extends AppCompatActivity {

    private AdView adView;
    private boolean isPremiumUser;

    FragmentManager fragmentManager;

    private enum FragmentState { Prepare, Training, Description };
    private FragmentState currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vision_field_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.vision_field);
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

        if (SettingsManager.isVisionFieldShowHelp()) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:
                startPrepareFragment();
                return true;
            case R.id.info: {
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

    public void startDescriptionFragment() {
        currentState = FragmentState.Description;

        fragmentManager.
                beginTransaction().
                replace(R.id.vision_field_fragment_container, new VisionFieldDescriptionFragment()).
                commit();
    }

    public void startTrainingFragment() {
        currentState = FragmentState.Training;

        currentFragment = new VisionFieldTrainingFragment();

        fragmentManager.
                beginTransaction().
                replace(R.id.vision_field_fragment_container, currentFragment).
                commit();
    }

    public void startPrepareFragment() {
        currentState = FragmentState.Prepare;

        fragmentManager.
                beginTransaction().
                replace(R.id.vision_field_fragment_container, new VisionFieldPrepareFragment()).
                commit();
    }
}
