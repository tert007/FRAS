package com.example.alexander.fastreading.app;

import android.app.Application;

import com.example.alexander.fastreading.app.RecordsManager;
import com.example.alexander.fastreading.app.SettingsManager;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by Alexander on 03.01.2017.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, "ca-app-pub-1214906094509332~3275388209");

        SettingsManager.Initialize(this);
        RecordsManager.Initialize(this);
    }
}
