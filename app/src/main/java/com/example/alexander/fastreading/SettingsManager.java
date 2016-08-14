package com.example.alexander.fastreading;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alexander.fastreading.shulte.ShulteTableType;

/**
 * Created by Alexander on 30.07.2016.
 */
public class SettingsManager {
    private static final String SETTINGS = "settings";

    private static final String SHULTE_TABLE_TYPE_FIELD = "shulte_table_type";
    private static final String SHULTE_TABLE_TYPE_FIELD_DEFAULT_VALUE = "numbers";

    private static final String GUESS_NUMBER_COMPLEXITY_FIELD = "guess_number_complexity";
    private static final int GUESS_NUMBER_COMPLEXITY_FIELD_DEFAULT_VALUE = 4;

    private static ShulteTableType shulteTableType = null;
    private static int guessNumberComplexity;

    private static SharedPreferences sharedPreferences;

    public static void Initialize(Context context){
        sharedPreferences = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public static void setShulteTableType(ShulteTableType tableType){
        sharedPreferences.edit().putString(SHULTE_TABLE_TYPE_FIELD, tableType.name().toLowerCase()).apply();
        shulteTableType = tableType;
    }

    public static ShulteTableType getShulteTableType() {
        if (shulteTableType == null){
            shulteTableType = ShulteTableType.valueOf(sharedPreferences.getString(SHULTE_TABLE_TYPE_FIELD, SHULTE_TABLE_TYPE_FIELD_DEFAULT_VALUE).toUpperCase());
        }

        return shulteTableType;
    }


    public static int getGuessNumberComplexity() {
        if (guessNumberComplexity == 0){
            guessNumberComplexity = sharedPreferences.getInt(GUESS_NUMBER_COMPLEXITY_FIELD, GUESS_NUMBER_COMPLEXITY_FIELD_DEFAULT_VALUE);
        }

        return guessNumberComplexity;
    }

    public static void setGuessNumberComplexity(int complexity) {
        sharedPreferences.edit().putInt(GUESS_NUMBER_COMPLEXITY_FIELD, complexity).apply();
        guessNumberComplexity = complexity;
    }

}
