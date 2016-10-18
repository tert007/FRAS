package com.example.alexander.fastreading.trainingmenu;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.RecordsManager;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.guessnumber.GuessNumberActivity;
import com.example.alexander.fastreading.shulte.ShulteActivity;

public class TrainingMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_menu_activity);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "ElMessiri-Regular.ttf");

        final TextView shulteTextView = (TextView) findViewById(R.id.training_menu_start_shulte);
        shulteTextView.setTypeface(typeface);
        shulteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrainingMenuActivity.this, ShulteActivity.class));
            }
        });

        final TextView guessNumberTextView = (TextView) findViewById(R.id.training_menu_start_guess_number);
        guessNumberTextView.setTypeface(typeface);
        guessNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrainingMenuActivity.this, GuessNumberActivity.class));
            }
        });
    }
}
