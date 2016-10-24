package com.example.alexander.fastreading.shulte.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.RecordsManager;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.shulte.ShulteGridAdapter;
import com.example.alexander.fastreading.shulte.ShulteActivity;
import com.example.alexander.fastreading.shulte.ShulteNumberGenerator;
import com.example.alexander.fastreading.shulte.TextViewOnTouchListener;

/**
 * Created by Alexander on 27.07.2016.
 */
public class ShulteMainFragment extends Fragment implements TextViewOnTouchListener {

    private static final String START_NEXT_ITEM_VALUE = "1";
    private static final String NOT_INITIALIZE_VALUE = "-";
    private static final int DEFAULT_COMPLEXITY = 5;

    private GridView gridView;

    private TextView recordTextView;
    private TextView nextItemTextView;
    private ShulteNumberGenerator shulteNumberGenerator;

    private Chronometer chronometer;

    private int countRow;
    private boolean permutation;

    private int previousColour;

    private int acceptGreen;
    private int rejectRed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countRow = SettingsManager.getShulteComplexity();
        permutation = SettingsManager.isShultePermutation();

        acceptGreen = ContextCompat.getColor(getActivity(), R.color.accept_green);
        rejectRed =  ContextCompat.getColor(getActivity(), R.color.reject_red);

        shulteNumberGenerator = new ShulteNumberGenerator(countRow);

        View view = inflater.inflate(R.layout.shulte_main_fragment, container, false);

        nextItemTextView = (TextView) view.findViewById(R.id.shulte_next_item);
        View recordView = view.findViewById(R.id.shulte_record_view);

        if (countRow != DEFAULT_COMPLEXITY) {
            recordView.setVisibility(View.GONE);
        }

        recordTextView = (TextView) recordView.findViewById(R.id.shulte_record);

        if(RecordsManager.getShulteRecord() == 0){
            recordTextView.setText(NOT_INITIALIZE_VALUE);
        } else {
            recordTextView.setText(String.valueOf(RecordsManager.getShulteRecord()));
        }

        ShulteGridAdapter shulteGridAdapter = new ShulteGridAdapter(getActivity(), R.id.shulte_grid_item_text_view, shulteNumberGenerator.getRandomNumbers());
        shulteGridAdapter.delegate = this;

        gridView = (GridView) view.findViewById(R.id.shulte_table);
        gridView.setNumColumns(countRow);
        gridView.setAdapter(shulteGridAdapter);

        if (SettingsManager.isShulteEyeMode()) {
            nextItemTextView.setText(NOT_INITIALIZE_VALUE);

            Button button = (Button) view.findViewById(R.id.shulte_eye_mode_button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);

                    long startTime = chronometer.getBase();
                    long finishTime = SystemClock.elapsedRealtime();

                    int timeElapsed = (int)((finishTime - startTime) / 1000);

                    if (countRow == DEFAULT_COMPLEXITY) {
                        builder.setTitle(R.string.training_completed);
                        int record = RecordsManager.getShulteRecord() ;
                        String recordSting = record == 1 ? String.valueOf(record) : NOT_INITIALIZE_VALUE;
                        builder.setMessage(getString(R.string.your_result) + ": " + timeElapsed + "\n" + getString(R.string.record) + ": " + recordSting);
                    } else {
                        builder.setTitle(R.string.training_completed);
                        builder.setMessage(getString(R.string.your_result) + ": " + timeElapsed);
                    }

                    builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((ShulteActivity) getActivity()).startTrainingFragment();
                        }
                    });
                    builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().onBackPressed();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else {
            nextItemTextView.setText(START_NEXT_ITEM_VALUE);
        }

        chronometer = (Chronometer) view.findViewById(R.id.shulte_chronometer);
        chronometer.start();

        return view;
    }

    @Override
    public boolean textViewOnTouch(TextView textView, MotionEvent motionEvent) {
        String pickedItemText = textView.getText().toString();
        String nextItemText = nextItemTextView.getText().toString();

        if (pickedItemText.equals(nextItemText)) {
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    previousColour = textView.getCurrentTextColor();

                    textView.setBackgroundColor(acceptGreen);
                    textView.setTextColor(Color.WHITE);

                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(previousColour);

                    String nextItem = shulteNumberGenerator.getNextNumberItem(pickedItemText);

                    if (nextItem == null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false);

                        chronometer.stop();

                        long startTime = chronometer.getBase();
                        long finishTime = SystemClock.elapsedRealtime();

                        int timeElapsed = (int)((finishTime - startTime) / 1000);

                        if (countRow == DEFAULT_COMPLEXITY) {
                            if (RecordsManager.getShulteRecord() > timeElapsed || RecordsManager.getShulteRecord() == 0){
                                RecordsManager.setShulteRecord(timeElapsed);

                                builder.setTitle(R.string.new_record);
                                builder.setMessage(getString(R.string.new_record) + ": " + timeElapsed);

                                recordTextView.setText(String.valueOf(timeElapsed));
                            } else {
                                builder.setTitle(R.string.training_completed);
                                builder.setMessage(getString(R.string.your_result) + ": " + timeElapsed + "\n" + getString(R.string.record) + ": " + RecordsManager.getShulteRecord());
                            }
                        } else {
                            builder.setTitle(R.string.training_completed);
                            builder.setMessage(getString(R.string.your_result) + ": " + timeElapsed);
                        }

                        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((ShulteActivity) getActivity()).startTrainingFragment();
                            }
                        });
                        builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((ShulteActivity) getActivity()).startSettingsFragment();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        nextItemTextView.setText(nextItem);

                        if (permutation) {
                            Handler handler = new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ShulteGridAdapter shulteGridAdapter = new ShulteGridAdapter(getActivity(), R.id.shulte_grid_item_text_view, shulteNumberGenerator.getRandomNumbers());
                                    shulteGridAdapter.delegate = ShulteMainFragment.this;

                                    gridView.setAdapter(shulteGridAdapter);
                                }
                            });

                            return false;
                        }
                    }
                    return false;
                default:
                    return false;
            }
        } else {
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    previousColour = textView.getCurrentTextColor();

                    textView.setBackgroundColor(rejectRed);
                    textView.setTextColor(Color.WHITE);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(previousColour);
                    return false;
                default:
                    return false;
            }
        }
    }
}
