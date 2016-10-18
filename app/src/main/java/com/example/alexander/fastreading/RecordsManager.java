package com.example.alexander.fastreading;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Alexander on 30.07.2016.
 */
public class RecordsManager {

    private static final String SHULTE_RECORD_FIELD = "shulte_record";
    private static final String GUESS_NUMBER_FIELD = "guess_number";

    private static int shulteRecord;
    private static int guessNumberRecord;

    private static SharedPreferences sharedPreferences;

    public static void Initialize(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setShulteRecord(int newRecord){
        sharedPreferences.edit().putInt(SHULTE_RECORD_FIELD, newRecord).apply();
        shulteRecord = newRecord;
    }

    public static int getShulteRecord() {
        if (shulteRecord == 0){
            shulteRecord = sharedPreferences.getInt(SHULTE_RECORD_FIELD, 0);
        }

        return shulteRecord;
    }

    public static int getGuessNumberRecord() {
        if (guessNumberRecord == 0){
            guessNumberRecord = sharedPreferences.getInt(GUESS_NUMBER_FIELD, 0);
        }

        return guessNumberRecord;
    }

    public static void setGuessNumberRecord(int newRecord) {
        sharedPreferences.edit().putInt(GUESS_NUMBER_FIELD, newRecord).apply();
        guessNumberRecord = newRecord;
    }
}
