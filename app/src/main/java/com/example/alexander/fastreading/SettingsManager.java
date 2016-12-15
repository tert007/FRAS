package com.example.alexander.fastreading;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Alexander on 30.07.2016.
 */
public class SettingsManager {
    private static final String GUESS_NUMBER_COMPLEXITY_KEY = "guess_number_complexity";
    private static final int GUESS_NUMBER_COMPLEXITY_DEFAULT_VALUE = 4;
    private static final String GUESS_NUMBER_SHOW_HELP_KEY = "guess_number_show_help";
    private static final boolean GUESS_NUMBER_HELP_DEFAULT_VALUE = true;

    private static final String SHULTE_COMPLEXITY_KEY = "shulte_complexity";
    private static final int SHULTE_COMPLEXITY_DEFAULT_VALUE = 5;
    private static final String SHULTE_PERMUTATION_KEY = "shulte_permutation";
    private static final boolean SHULTE_PERMUTATION_DEFAULT_VALUE = false;
    private static final String SHULTE_COLORED_KEY = "shulte_colored";
    private static final boolean SHULTE_COLORED_DEFAULT_VALUE = false;
    private static final String SHULTE_EYE_MODE_KEY = "shulte_eye_mode";
    private static final boolean SHULTE_EYE_MODE_DEFAULT_VALUE = false;
    private static final String SHULTE_SHOW_HELP_KEY = "shulte_show_help";
    private static final boolean SHULTE_SHOW_HELP_DEFAULT_VALUE = true;

    private static final String VISION_FIELD_COMPLEXITY_KEY = "vision_field_complexity";
    private static final int VISION_FIELD_COMPLEXITY_DEFAULT_VALUE = 30_000;
    private static final String VISION_FIELD_SHOW_DELAY_KEY = "vision_field_show_delay";
    private static final int VISION_FIELD_SHOW_DELAY_DEFAULT_VALUE = 1000;
    private static final String VISION_FIELD_SHOW_HELP_KEY = "vision_field_show_help";
    private static final boolean VISION_FIELD_SHOW_HELP_DEFAULT_VALUE = true;

    private static final String SPEED_READING_SHOW_HELP = "speed_reading_show_help";
    private static final boolean SPEED_READING_SHOW_HELP_DEFAULT_VALUE = true;

    private static int guessNumberComplexity;
    private static boolean guessNumberShowHelp;

    private static int shulteComplexity;
    private static boolean shultePermutation;
    private static boolean shulteColored;
    private static boolean shulteEyeMode;
    private static boolean shulteShowHelp;

    private static int visionFieldComplexity;
    private static int visionFieldShowDelay;
    private static boolean visionFieldShowHelp;

    private static boolean speedReadingShowHelp;

    private static SharedPreferences sharedPreferences;

    public static void Initialize(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isGuessNumberShowHelp() {
        if (guessNumberShowHelp == false) {
            guessNumberShowHelp = sharedPreferences.getBoolean(GUESS_NUMBER_SHOW_HELP_KEY, GUESS_NUMBER_HELP_DEFAULT_VALUE);
        }

        return guessNumberShowHelp;
    }

    public static void setGuessNumberShowHelp(boolean showHelp) {
        sharedPreferences.edit().putBoolean(GUESS_NUMBER_SHOW_HELP_KEY, showHelp).apply();
        guessNumberShowHelp = showHelp;
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

    public static boolean isShultePermutation() {
        if (shultePermutation == false) {
            shultePermutation = sharedPreferences.getBoolean(SHULTE_PERMUTATION_KEY, SHULTE_PERMUTATION_DEFAULT_VALUE);
        }

        return shultePermutation;
    }

    public static void setShultePermutation(boolean permutation) {
        sharedPreferences.edit().putBoolean(SHULTE_PERMUTATION_KEY, permutation).apply();
        shultePermutation = permutation;
    }

    public static boolean isShulteColored() {
        if (shulteColored == false) {
            shulteColored = sharedPreferences.getBoolean(SHULTE_COLORED_KEY, SHULTE_COLORED_DEFAULT_VALUE);
        }

        return shulteColored;
    }

    public static void setShulteColored(boolean colored) {
        sharedPreferences.edit().putBoolean(SHULTE_COLORED_KEY, colored).apply();
        shulteColored = colored;
    }

    public static boolean isShulteEyeMode() {
        if (shulteEyeMode == false) {
            shulteEyeMode = sharedPreferences.getBoolean(SHULTE_EYE_MODE_KEY, SHULTE_EYE_MODE_DEFAULT_VALUE);
        }

        return shulteEyeMode;
    }

    public static void setShulteEyeMode(boolean eyeMode) {
        sharedPreferences.edit().putBoolean(SHULTE_EYE_MODE_KEY, eyeMode).apply();
        shulteEyeMode = eyeMode;
    }

    public static boolean isShulteShowHelp() {
        if (shulteShowHelp == false) {
            shulteShowHelp = sharedPreferences.getBoolean(SHULTE_SHOW_HELP_KEY, SHULTE_SHOW_HELP_DEFAULT_VALUE);

        }
        return shulteShowHelp;
    }

    public static void setShulteShowHelp(boolean showHelp) {
        sharedPreferences.edit().putBoolean(SHULTE_SHOW_HELP_KEY, showHelp).apply();
        shulteShowHelp = showHelp;
    }

    public static boolean isVisionFieldShowHelp() {
        if (visionFieldShowHelp == false) {
            visionFieldShowHelp = sharedPreferences.getBoolean(VISION_FIELD_SHOW_HELP_KEY, VISION_FIELD_SHOW_HELP_DEFAULT_VALUE);
        }
        return visionFieldShowHelp;
    }

    public static void setVisionFieldShowHelp(boolean showHelp) {
        sharedPreferences.edit().putBoolean(VISION_FIELD_SHOW_HELP_KEY, showHelp).apply();
        visionFieldShowHelp = showHelp;
    }

    public static int getVisionFieldComplexity() {
        if (visionFieldComplexity == 0) {
            visionFieldComplexity = sharedPreferences.getInt(VISION_FIELD_COMPLEXITY_KEY, VISION_FIELD_COMPLEXITY_DEFAULT_VALUE);
        }

        return visionFieldComplexity;
    }

    public static void setVisionFieldComplexity(int complexity) {
        sharedPreferences.edit().putInt(VISION_FIELD_COMPLEXITY_KEY, complexity).apply();
        visionFieldComplexity = complexity;
    }

    public static int getVisionFieldShowDelay() {
        if (visionFieldShowDelay == 0) {
            visionFieldShowDelay = sharedPreferences.getInt(VISION_FIELD_SHOW_DELAY_KEY, VISION_FIELD_SHOW_DELAY_DEFAULT_VALUE);
        }

        return visionFieldShowDelay;
    }

    public static void setVisionFieldShowDelay(int showDelay) {
        sharedPreferences.edit().putInt(VISION_FIELD_SHOW_DELAY_KEY, showDelay).apply();
        visionFieldShowDelay = showDelay;
    }

    public static boolean isSpeedReadingShowHelp() {
        if (speedReadingShowHelp == false) {
            speedReadingShowHelp = sharedPreferences.getBoolean(SPEED_READING_SHOW_HELP, SPEED_READING_SHOW_HELP_DEFAULT_VALUE);
        }

        return speedReadingShowHelp;
    }

    public static void setSpeedReadingShowHelp(boolean showHelp) {
        sharedPreferences.edit().putBoolean(SPEED_READING_SHOW_HELP, showHelp).apply();
        speedReadingShowHelp = showHelp;
    }
}
