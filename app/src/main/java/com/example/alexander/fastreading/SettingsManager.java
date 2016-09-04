package com.example.alexander.fastreading;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alexander.fastreading.shulte.ShulteTableType;

import java.io.File;

/**
 * Created by Alexander on 30.07.2016.
 */
public class SettingsManager {
    private static final String SETTINGS = "settings";

    private static final String SHULTE_TABLE_TYPE_KEY = "shulte_table_type";
    private static final String SHULTE_TABLE_TYPE_DEFAULT_VALUE = "numbers";

    private static final String GUESS_NUMBER_COMPLEXITY_KEY = "guess_number_complexity";
    private static final int GUESS_NUMBER_COMPLEXITY_DEFAULT_VALUE = 4;

    private static final String READER_TEXT_SIZE_KEY = "reader_text_size";
    private static final int READER_TEXT_SIZE_DEFAULT_VALUE = 12;

    private static ShulteTableType shulteTableType = null;
    private static int guessNumberComplexity;
    private static int readerTextSize;

    private static Context context;
    private static SharedPreferences sharedPreferences;

    public static void Initialize(Context cont){
        context = cont;
        sharedPreferences = cont.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public static void setShulteTableType(ShulteTableType tableType){
        sharedPreferences.edit().putString(SHULTE_TABLE_TYPE_KEY, tableType.name().toLowerCase()).apply();
        shulteTableType = tableType;
    }

    public static ShulteTableType getShulteTableType() {
        if (shulteTableType == null){
            shulteTableType = ShulteTableType.valueOf(sharedPreferences.getString(SHULTE_TABLE_TYPE_KEY, SHULTE_TABLE_TYPE_DEFAULT_VALUE).toUpperCase());
        }

        return shulteTableType;
    }

    public static void setReaderTextSize(int textSize) {
        sharedPreferences.edit().putInt(READER_TEXT_SIZE_KEY, textSize).apply();
        readerTextSize = textSize;
    }

    public static int getReaderTextSize() {
        if (readerTextSize == 0){
            readerTextSize = sharedPreferences.getInt(READER_TEXT_SIZE_KEY, READER_TEXT_SIZE_DEFAULT_VALUE);
        }

        return readerTextSize;
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



    public static String getTempPath(){
        return context.getApplicationInfo().dataDir + File.separator + "temp";
    }

}
