package com.example.alexander.fastreading;

import android.view.View;

/**
 * Created by Alexander on 29.07.2016.
 */
public interface ViewOnClickListener {
    //Позволяет делегировать нажатие во фрагменте другому классу (В данном случае Activity)
    void viewOnClick(View v);
}
