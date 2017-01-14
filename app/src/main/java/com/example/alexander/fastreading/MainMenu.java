package com.example.alexander.fastreading;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.example.alexander.fastreading.motivator.MotivatorActivity;
import com.example.alexander.fastreading.reader.library.LibraryActivity;
import com.example.alexander.fastreading.reader.reader.ReaderActivity;
import com.example.alexander.fastreading.trainingmenu.TrainingMenuActivity;
import com.google.android.gms.ads.MobileAds;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Typeface typeface = Typeface.createFromAsset(this.getAssets(), "ElMessiri-Regular.ttf");

        final TextView startReaderTextView = (TextView) findViewById(R.id.main_menu_start_reading_text_view);
        //startReaderTextView.setTypeface(typeface);
        startReaderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, LibraryActivity.class));
            }
        });


        final TextView startTrainingTextView = (TextView) findViewById(R.id.main_menu_start_training_text_view);
        //startTrainingTextView.setTypeface(typeface);
        startTrainingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, TrainingMenuActivity.class));
            }
        });

        final TextView startMotivatorsTextView = (TextView) findViewById(R.id.main_menu_motivators_text_view);
        //startTrainingTextView.setTypeface(typeface);
        startMotivatorsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMenu.this, MotivatorActivity.class));
            }
        });


        //
        final View startDescription = findViewById(R.id.main_menu_description_image_view);
        startDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, MainMenuDescriptionActivity.class);
                startActivity(intent);
            }
        });


        final View shareView = findViewById(R.id.main_menu_share_image_view);
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

                String shareText = "https://play.google.com/store/apps/details?id=com.speedreading.alexander.speedreading";
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                startActivity(sendIntent);
            }
        });

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
