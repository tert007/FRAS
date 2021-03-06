package com.example.alexander.fastreading.shulte.fragment;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.RecordsManager;
import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.Training;
import com.example.alexander.fastreading.shulte.ShulteActivity;
import com.example.alexander.fastreading.shulte.ShulteGridAdapter;
import com.example.alexander.fastreading.shulte.ShulteNumberGenerator;
import com.example.alexander.fastreading.shulte.TextViewOnTouchListener;

public class ShulteTrainingFragment extends Fragment implements TextViewOnTouchListener, Training {

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

        final TextView eyesModeEnabled = (TextView) view.findViewById(R.id.shulte_main_eyes_mode_enabled_text_view);

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

            eyesModeEnabled.setVisibility(View.VISIBLE);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    chronometer.stop();

                    currentState = FragmentState.ShowResult;

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
                            ((ShulteActivity) getActivity()).startPrepareFragment();
                        }
                    });
                    builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
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

    private long chronometerStoppedTime;

    private enum FragmentState { ShowResult, Game, PauseFragmentDialog, ExitFragmentDialog }

    private FragmentState currentState;

    AlertDialog dialog;

    @Override
    public void onPause() {
        if (currentState == FragmentState.ExitFragmentDialog || currentState == FragmentState.PauseFragmentDialog) {
            dialog.dismiss();
        }

        if (currentState != FragmentState.ShowResult) {

            if (currentState == FragmentState.Game) {
                chronometerStoppedTime = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();
            }

            currentState = FragmentState.PauseFragmentDialog;
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentState == FragmentState.PauseFragmentDialog) // защита от первого вывода
            showPauseDialog();
    }

    private void showPauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);

        builder.setMessage(R.string.training_is_paused);

        builder.setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                currentState = FragmentState.Game;

                chronometer.setBase(SystemClock.elapsedRealtime() + chronometerStoppedTime);
                chronometer.start();
            }
        });

        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void trainingOnBackPressed() {
        chronometerStoppedTime = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();

        currentState = FragmentState.ExitFragmentDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);

        builder.setMessage(R.string.exit_message);

        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                currentState = FragmentState.Game;

                chronometer.setBase(SystemClock.elapsedRealtime() + chronometerStoppedTime);
                chronometer.start();
            }
        });

        dialog = builder.create();
        dialog.show();
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

                    textView.playSoundEffect(android.view.SoundEffectConstants.CLICK);

                    String nextItem = shulteNumberGenerator.getNextNumberItem(pickedItemText);

                    if (nextItem == null){
                        currentState = FragmentState.ShowResult;

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
                                ((ShulteActivity) getActivity()).startPrepareFragment();
                            }
                        });
                        builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
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
                                    shulteGridAdapter.delegate = ShulteTrainingFragment.this;

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
                    textView.playSoundEffect(android.view.SoundEffectConstants.CLICK);

                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(previousColour);
                    return false;
                default:
                    return false;
            }
        }
    }
}
