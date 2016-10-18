package com.example.alexander.fastreading.guessnumber;

/**
 * Created by Alexander on 23.07.2016.
 */
public interface Response {
    //После того как AsyncTask сгенерирует случайное число, оно передает его с помощью этого метода
    void onResponse(int[] result);
}
