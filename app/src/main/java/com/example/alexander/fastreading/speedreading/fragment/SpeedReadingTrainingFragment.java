package com.example.alexander.fastreading.speedreading.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.Training;
import com.example.alexander.fastreading.speedreading.SpeedReadingActivity;
import com.example.alexander.fastreading.speedreading.SpeedReadingLastWordResponse;

import java.util.Random;

public class SpeedReadingTrainingFragment extends Fragment implements Training {

    public SpeedReadingLastWordResponse lastWordDelegate;

    private static final Random random = new Random();

    private static final int COUNT_TRY = 20;

    private static final int TEXT_VIEWS_COUNT = 9;

    private static final int DEFAULT_RANDOM_OFFSET = 8;
    private static final int DEFAULT_RANDOM_COUNT = 12;

    TextView[] textViews;
    String[] wordsBase;

    int wordCount;
    int textViewIndex = 0;
    int wordIndex = 0;
    String lastWord = "";
    String randomWord = "";

    SpeedReadingActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speed_reading_main_fragment, null);

        textViews = new TextView[TEXT_VIEWS_COUNT];

        textViews[0] = (TextView) view.findViewById(R.id.speed_reading_word_view_1);
        textViews[1] = (TextView) view.findViewById(R.id.speed_reading_word_view_2);
        textViews[2] = (TextView) view.findViewById(R.id.speed_reading_word_view_3);
        textViews[3] = (TextView) view.findViewById(R.id.speed_reading_word_view_4);
        textViews[4] = (TextView) view.findViewById(R.id.speed_reading_word_view_5);
        textViews[5] = (TextView) view.findViewById(R.id.speed_reading_word_view_6);
        textViews[6] = (TextView) view.findViewById(R.id.speed_reading_word_view_7);
        textViews[7] = (TextView) view.findViewById(R.id.speed_reading_word_view_8);
        textViews[8] = (TextView) view.findViewById(R.id.speed_reading_word_view_9);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setMax(COUNT_TRY);

        TextView speedTextView = (TextView) view.findViewById(R.id.speed_reading_main_speed_text_view);

        wordsBase = getResources().getStringArray(R.array.speed_reading_words);
        wordCount = getRandomNumber();

        mainActivity = (SpeedReadingActivity) getActivity();
        speedTextView.setText(String.valueOf(mainActivity.speeds[mainActivity.index]));

        progressBar.setProgress(mainActivity.countAnswer);

        if(COUNT_TRY == mainActivity.countAnswer){
            showResult();
        } else {
            handler.postDelayed(wordDelayed, 1000);
        }

        return view;
    }

    private int getRandomNumber() {
        int number = random.nextInt(DEFAULT_RANDOM_COUNT);
        return number + DEFAULT_RANDOM_OFFSET;
    }

    private int getRandomArrayKey(String[] array){
        return random.nextInt(array.length);
    }

    private Handler handler = new Handler();
    private Runnable wordDelayed = new Runnable() {
        @Override
        public void run() {
            if (isPaused) {
                return;
            }

            if (wordIndex < wordCount) {
                int randomKey = getRandomArrayKey(wordsBase);

                if (textViewIndex > 0)
                    textViews[textViewIndex - 1].setText("");

                if (textViewIndex == 0)
                    textViews[TEXT_VIEWS_COUNT - 1].setText("");

                randomWord = wordsBase[randomKey];
                textViews[textViewIndex].setText(randomWord);
                textViewIndex++;

                if (textViewIndex == TEXT_VIEWS_COUNT) {
                    textViewIndex = 0;
                }
                wordIndex++;

                handler.postDelayed(this, mainActivity.speed);
            } else {
                lastWord = randomWord;
                lastWordDelegate.onLastWordResponse(lastWord);
            }
        }
    };

    AlertDialog dialog;

    private boolean isPaused;

    @Override
    public void onPause() {
        isPaused = true;
        handler.removeCallbacks(wordDelayed);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isPaused) {
            if (dialog == null) {
                showOnResumeDialog();
            }
        }
    }

    @Override
    public void trainingOnBackPressed() {
        if (dialog != null) {
            return;
        }

        isPaused = true;

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

                isPaused = false;
                dialog = null;

                handler.postDelayed(wordDelayed, 1000);
            }
        });

        dialog = builder.create();
        dialog.show();
    }


    private void showResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.training_completed);

        String resultMessage = getString(R.string.your_result) + ": " + mainActivity.speeds[mainActivity.index];
        builder.setMessage(resultMessage);

        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                SpeedReadingActivity activity = (SpeedReadingActivity)getActivity();

                activity.countAnswer = 0;
                activity.index = 2;
                activity.speed = activity.speeds[activity.index];

                activity.startPrepareFragment();
            }
        });

        builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void showOnResumeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);

        builder.setMessage(R.string.training_is_paused);

        builder.setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                isPaused = false;
                dialog = null;

                handler.postDelayed(wordDelayed, 1000);
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


}
