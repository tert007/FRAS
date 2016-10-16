package com.example.alexander.fastreading.shulte.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.RecordsManager;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.shulte.GridAdapter;
import com.example.alexander.fastreading.shulte.ShulteGenerator;
import com.example.alexander.fastreading.shulte.ShulteTableType;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteMainFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String NOT_INITIALIZE_VALUE = "-";

    private TextView recordTextView;
    private TextView nextItemTextView;
    private GridView gridView;
    private GridAdapter gridAdapter;

    private Chronometer chronometer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int countRow = getArguments().getInt("count_row");

        View view = inflater.inflate(R.layout.shulte_main_fragment, container, false);

        nextItemTextView = (TextView) view.findViewById(R.id.shulte_next_item);

        if (SettingsManager.getShulteTableType().equals(ShulteTableType.NUMBERS)){
            nextItemTextView.setText(ShulteGenerator.FIRST_NUMBER);
            gridAdapter = new GridAdapter(getActivity().getApplicationContext(), R.id.shulte_grid_item_text_view, ShulteGenerator.getRandomNumbers());
        } else {
            nextItemTextView.setText(ShulteGenerator.FIRST_LETTER);
            gridAdapter = new GridAdapter(getActivity().getApplicationContext(), R.id.shulte_grid_item_text_view, ShulteGenerator.getRandomLetters());
        }

        recordTextView = (TextView) view.findViewById(R.id.shulte_record);
        if(RecordsManager.getShulteRecord() == 0){
            recordTextView.setText(NOT_INITIALIZE_VALUE);
        } else {
            recordTextView.setText(String.valueOf(RecordsManager.getShulteRecord()));
        }

        gridView = (GridView) view.findViewById(R.id.shulte_table);
        gridView.setNumColumns(countRow);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(gridAdapter);
        
        chronometer = (Chronometer) view.findViewById(R.id.shulte_chronometer);
        chronometer.start();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final TextView pickedItem = (TextView)view.findViewById(R.id.shulte_grid_item_text_view);

        String pickedItemText = pickedItem.getText().toString();
        String nextItemText = nextItemTextView.getText().toString();

        if (pickedItemText.equals(nextItemText)){
            pickedItem.setBackgroundColor(Color.GREEN);

            String nextItem;
            if (SettingsManager.getShulteTableType().equals(ShulteTableType.NUMBERS)){
                nextItem = ShulteGenerator.getNextNumberItem(pickedItemText);
            } else {
                nextItem = ShulteGenerator.getNextLetterItem(pickedItemText);
            }

            if (nextItem == null){
                long startTime = SystemClock.elapsedRealtime();

                chronometer.stop();

                long finishTime = chronometer.getBase();
                int timeElapsed = (int)((finishTime - startTime) / 1000L);

                if (RecordsManager.getShulteRecord() > timeElapsed || RecordsManager.getShulteRecord() == 0){
                    RecordsManager.setShulteRecord(timeElapsed);

                    recordTextView.setText(String.valueOf(timeElapsed));
                }

                nextItemTextView.setText(NOT_INITIALIZE_VALUE);

            } else {
                nextItemTextView.setText(nextItem);
            }
        } else {
            pickedItem.setBackgroundColor(Color.RED);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pickedItem.setBackgroundColor(Color.WHITE);
            }
        }, 50);
    }
}
