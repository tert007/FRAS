package com.example.alexander.fastreading.shulte;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by Alexander on 19.10.2016.
 */
public class ShulteColourGenerator {
    private static Random random = new Random();
    private static final int COUNT_COLOURS = 5;

    public static int getColour() {
        int colorIndex = random.nextInt(COUNT_COLOURS);
        switch (colorIndex) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.parseColor("#311B92"); //Purple или синий
            case 2:
                return Color.parseColor("#1B5E20"); //Green 900
            case 3:
                return Color.parseColor("#FFAB110E"); //RED
            default:
                return Color.parseColor("#FFA17222"); //Pink
        }
    }
}
