package com.example.alexander.fastreading;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Alexander on 30.07.2016.
 */
public class SettingsManager {
    private static final String SETTINGS = "settings";

    private static final String GUESS_NUMBER_COMPLEXITY_KEY = "guess_number_complexity";
    private static final int GUESS_NUMBER_COMPLEXITY_DEFAULT_VALUE = 4;

    private static final String SHULTE_COMPLEXITY_KEY = "guess_number_complexity";
    private static final int SHULTE_COMPLEXITY_DEFAULT_VALUE = 5;

    private static final String READER_TEXT_SIZE_KEY = "reader_text_size";
    private static final int READER_TEXT_SIZE_DEFAULT_VALUE = 12;

    private static int guessNumberComplexity;
    private static int shulteComplexity;

    private static int readerTextSize;

    private static SharedPreferences sharedPreferences;

    public static void Initialize(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*
    public static void setReaderTextSize(int textSize) {
        sharedPreferences.edit().putInt(READER_TEXT_SIZE_KEY, textSize).apply();
        readerTextSize = textSize;
    }*/

    /*
    public static int getReaderTextSize() {
        if (readerTextSize == 0){
            readerTextSize = sharedPreferences.getInt(READER_TEXT_SIZE_KEY, READER_TEXT_SIZE_DEFAULT_VALUE);
        }

        return readerTextSize;
    }
    */

    public static int getShulteComplexity() {
        if (shulteComplexity == 0){
            shulteComplexity = sharedPreferences.getInt(SHULTE_COMPLEXITY_KEY, SHULTE_COMPLEXITY_DEFAULT_VALUE);
        }

        return shulteComplexity;
    }

    public static void setShulteComplexity(int complexity) {
        sharedPreferences.edit().putInt(SHULTE_COMPLEXITY_KEY, complexity).apply();
        shulteComplexity = complexity;
    }

    public static int getGuessNumberComplexity() {
        if (guessNumberComplexity == 0){
            guessNumberComplexity = sharedPreferences.getInt(GUESS_NUMBER_COMPLEXITY_KEY, GUESS_NUMBER_COMPLEXITY_DEFAULT_VALUE);
        }

        return guessNumberComplexity;
    }

    public static void setGuessNumberComplexity(int complexity) {
        sharedPreferences.edit().putInt(GUESS_NUMBER_COMPLEXITY_KEY, complexity).apply();
        guessNumberComplexity = complexity;
    }
}
