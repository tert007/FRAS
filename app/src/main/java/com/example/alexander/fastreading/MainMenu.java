package com.example.alexander.fastreading;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.alexander.fastreading.reader.library.LibraryActivity;
import com.example.alexander.fastreading.reader.reader.ReaderActivity;
import com.example.alexander.fastreading.trainingmenu.TrainingMenuActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "ElMessiri-Regular.ttf");

        final TextView startReaderTextView = (TextView) findViewById(R.id.main_menu_start_reading_text_view);
        startReaderTextView.setTypeface(typeface);
        startReaderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, LibraryActivity.class));
            }
        });


        final TextView startTrainingTextView = (TextView) findViewById(R.id.main_menu_start_training_text_view);
        startTrainingTextView.setTypeface(typeface);
        startTrainingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, TrainingMenuActivity.class));
            }
        });

        SettingsManager.Initialize(this);
        RecordsManager.Initialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shulte_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
