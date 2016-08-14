package com.example.alexander.fastreading.trainingmenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.RecordsManager;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.guessnumber.GuessNumberActivity;
import com.example.alexander.fastreading.shulte.ShulteActivity;

public class TrainingMenuActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.activity_start_shulte_table);
        button.setOnClickListener(this);

        button2 = (Button) findViewById(R.id.activity_start_guess_number);
        button2.setOnClickListener(this);

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
            case R.id.activity_start_shulte_table :
                startActivity(new Intent(this, ShulteActivity.class));
                break;
            case R.id.activity_start_guess_number :
                startActivity(new Intent(this, GuessNumberActivity.class));
                break;
        }
    }
}
