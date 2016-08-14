package com.example.alexander.fastreading;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alexander on 30.07.2016.
 */
public class RecordsManager {
    private static final String RECORS = "records";
    private static final String SHULTE_RECORD_FIELD = "shulte_record";

    private static int shulteRecord;

    private static SharedPreferences sharedPreferences;

    public static void Initialize(Context context){
        sharedPreferences = context.getSharedPreferences(RECORS, Context.MODE_PRIVATE);
    }

    public static void setShulteRecord(int newRecod){
        sharedPreferences.edit().putInt(SHULTE_RECORD_FIELD, newRecod).apply();
        shulteRecord = newRecod;
    }

    public static int getShulteRecord() {
        if (shulteRecord == 0){
            shulteRecord = sharedPreferences.getInt(SHULTE_RECORD_FIELD, 0);
        }

        return shulteRecord;
    }
}
