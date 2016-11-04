package com.example.alexander.fastreading.speedreading;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.speedreading.fragment.SpeedReadingMainFragment;
import com.example.alexander.fastreading.speedreading.fragment.SpeedReadingAnswersFragment;


public class SpeedReadingActivity extends AppCompatActivity implements SpeedReadingLastWordResponse {

    FragmentManager fragmentManager;

    public int countAnswer = 0;
    public final int[] wordsPerMinute = new int[]{
            100,
            150,
            200,
            250,
            300,
            350,
            400,
            450,
            500,
            550,
            600,
            650,
            700,
            750,
            800,
            850,
            900,
            950,
            1000
    };

    public int index = 2;
    public int speed = 60_000 / wordsPerMinute[index];

    public void setSpeed(boolean incr){
        if(incr){
            if(index < wordsPerMinute.length)
                index++;
            speed = 60_000 / wordsPerMinute[index];
        }
        else{
            if(index > 0)
                index--;
            speed = 60_000 / wordsPerMinute[index];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_reading_activity);

        fragmentManager = getFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        startExercise();
    }

    @Override
    public void onLastWordResponse(String lastWord) {
        SpeedReadingAnswersFragment answersFragment = new SpeedReadingAnswersFragment();

        Bundle bundle = new Bundle();
        bundle.putString("last_word", lastWord);

        answersFragment.setArguments(bundle);

        fragmentManager.beginTransaction().
                replace(R.id.speed_reading_fragment_container, answersFragment).
                commit();
    }

    public void startExercise(){
        SpeedReadingMainFragment mainFragment = new SpeedReadingMainFragment();
        mainFragment.lastWordDelegate = this;

        fragmentManager.beginTransaction().
                replace(R.id.speed_reading_fragment_container, mainFragment).
                commit();
    }

}
