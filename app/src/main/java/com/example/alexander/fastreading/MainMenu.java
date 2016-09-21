package com.example.alexander.fastreading;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.fastreading.reader.ReaderActivity;
import com.example.alexander.fastreading.trainingmenu.TrainingMenuActivity;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView startTrainingMenuImageView = (ImageView) findViewById(R.id.main_menu_start_training_menu_text_view);
        startTrainingMenuImageView.setOnClickListener(this);

        ImageView startReaderImageView = (ImageView) findViewById(R.id.main_menu_start_reader_text_view);
        startReaderImageView.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_menu_start_training_menu_text_view :
                startActivity(new Intent(this, TrainingMenuActivity.class));
                break;
            case R.id.main_menu_start_reader_text_view :
                startActivity(new Intent(this, ReaderActivity.class));
                break;
        }
    }
}
