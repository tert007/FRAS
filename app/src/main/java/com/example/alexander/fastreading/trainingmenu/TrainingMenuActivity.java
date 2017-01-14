package com.example.alexander.fastreading.trainingmenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.guessnumber.GuessNumberActivity;
import com.example.alexander.fastreading.shulte.ShulteActivity;
import com.example.alexander.fastreading.speedreading.SpeedReadingActivity;
import com.example.alexander.fastreading.visionfield.VisionFieldActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class TrainingMenuActivity extends AppCompatActivity {

    InterstitialAd interstitialAd;
    private boolean isPremiumUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_menu_activity);

        isPremiumUser = SettingsManager.isPremiumUser();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-1214906094509332/5949653003");

        requestNewInterstitial();

        //Typeface typeface = Typeface.createFromAsset(this.getAssets(), "ElMessiri-Regular.ttf");

        final TextView shulteTextView = (TextView) findViewById(R.id.training_menu_start_shulte);
        //shulteTextView.setTypeface(typeface);
        shulteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPremiumUser) {
                    startActivity(new Intent(TrainingMenuActivity.this, ShulteActivity.class));
                } else {

                    if (interstitialAd.isLoaded()) {
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                startActivity(new Intent(TrainingMenuActivity.this, ShulteActivity.class));
                            }
                        });

                        interstitialAd.show();
                    } else {
                        startActivity(new Intent(TrainingMenuActivity.this, ShulteActivity.class));
                    }
                }
            }
        });

        final TextView guessNumberTextView = (TextView) findViewById(R.id.training_menu_start_guess_number);
        //guessNumberTextView.setTypeface(typeface);
        guessNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPremiumUser) {
                    startActivity(new Intent(TrainingMenuActivity.this, GuessNumberActivity.class));
                } else {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                startActivity(new Intent(TrainingMenuActivity.this, GuessNumberActivity.class));
                            }
                        });

                        interstitialAd.show();
                    } else {
                        startActivity(new Intent(TrainingMenuActivity.this, GuessNumberActivity.class));
                    }
                }
            }
        });

        final TextView visionFieldTextView = (TextView) findViewById(R.id.training_menu_start_vision_field);
        //visionFieldTextView.setTypeface(typeface);
        visionFieldTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPremiumUser) {
                    startActivity(new Intent(TrainingMenuActivity.this, VisionFieldActivity.class));
                } else {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                startActivity(new Intent(TrainingMenuActivity.this, VisionFieldActivity.class));
                            }
                        });

                        interstitialAd.show();
                    } else {
                        startActivity(new Intent(TrainingMenuActivity.this, VisionFieldActivity.class));
                    }
                }

            }
        });

        final TextView speedReadingTextView = (TextView) findViewById(R.id.training_menu_start_speed_reading);
        //speedReadingTextView.setTypeface(typeface);
        speedReadingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPremiumUser) {
                    startActivity(new Intent(TrainingMenuActivity.this, SpeedReadingActivity.class));

                } else {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                startActivity(new Intent(TrainingMenuActivity.this, SpeedReadingActivity.class));
                            }
                        });

                        interstitialAd.show();
                    } else {
                        startActivity(new Intent(TrainingMenuActivity.this, SpeedReadingActivity.class));
                    }
                }
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitialAd.loadAd(adRequest);
    }

}
