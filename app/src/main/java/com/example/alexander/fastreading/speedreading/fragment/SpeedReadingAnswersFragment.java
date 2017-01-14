package com.example.alexander.fastreading.speedreading.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.Training;
import com.example.alexander.fastreading.speedreading.SpeedReadingActivity;

import java.util.Arrays;
import java.util.Random;

public class SpeedReadingAnswersFragment extends Fragment implements View.OnClickListener, Training {
    private String answerWord;

    private static final Random random = new Random();

    private static final int ANSWERS_TEXT_VIEW_COUNT = 6;
    Button[] buttons;
    TextView speedTextView;
    SpeedReadingActivity mainActivity;
    int indexTrueAnswer;

    private int acceptGreen;
    private int rejectRed;

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        acceptGreen = ContextCompat.getColor(getActivity(), R.color.accept_green);
        rejectRed  = ContextCompat.getColor(getActivity(), R.color.reject_red);

        View view = inflater.inflate(R.layout.speed_reading_answer_fragment, null);

        buttons = new Button[ANSWERS_TEXT_VIEW_COUNT];

        buttons[0] = (Button) view.findViewById(R.id.speed_reading_answer_button_1);
        buttons[1] = (Button) view.findViewById(R.id.speed_reading_answer_button_2);
        buttons[2] = (Button) view.findViewById(R.id.speed_reading_answer_button_3);
        buttons[3] = (Button) view.findViewById(R.id.speed_reading_answer_button_4);
        buttons[4] = (Button) view.findViewById(R.id.speed_reading_answer_button_5);
        buttons[5] = (Button) view.findViewById(R.id.speed_reading_answer_button_6);

        speedTextView = (TextView) view.findViewById(R.id.speed_reading_answer_speed_text_view);

        mainActivity = (SpeedReadingActivity) getActivity();

        answerWord = getArguments().getString("true_answer");

        speedTextView.setText(String.valueOf(mainActivity.speeds[mainActivity.index]));
        indexTrueAnswer = getRandomAnswersIndex();
        String[] words = getRandomWords(ANSWERS_TEXT_VIEW_COUNT, answerWord, indexTrueAnswer);

        for (int i = 0; i < buttons.length; i++){
            buttons[i].setText(words[i]);
            buttons[i].setOnClickListener(this);
        }

        handler = new Handler();

        return view;
    }


    @Override
    public void onClick(View v) {
        int selectedIndex = 0;
        switch (v.getId()){
            case R.id.speed_reading_answer_button_1:
                selectedIndex = 0;
                break;
            case R.id.speed_reading_answer_button_2:
                selectedIndex = 1;
                break;
            case R.id.speed_reading_answer_button_3:
                selectedIndex = 2;
                break;
            case R.id.speed_reading_answer_button_4:
                selectedIndex = 3;
                break;
            case R.id.speed_reading_answer_button_5:
                selectedIndex = 4;
                break;
            case R.id.speed_reading_answer_button_6:
                selectedIndex = 5;
                break;
        }

        for (int i = 0; i < buttons.length; i++){
            buttons[i].setEnabled(false);
        }

        if(selectedIndex == indexTrueAnswer) {
            mainActivity.countAnswer++;
            mainActivity.updateSpeed(true);
            speedTextView.setText(String.valueOf(mainActivity.speeds[mainActivity.index]));
            speedTextView.setTextColor(acceptGreen);

            isAnswered = true;

            //currentState = FragmentState.CheckResultRunnable;
            handler.postDelayed(checkResultRunnable, 1000);
        } else {
            mainActivity.countAnswer++;
            mainActivity.updateSpeed(false);
            speedTextView.setText(String.valueOf(mainActivity.speeds[mainActivity.index]));
            speedTextView.setTextColor(rejectRed);

            isAnswered = true;

            //currentState = FragmentState.CheckResultRunnable;
            handler.postDelayed(checkResultRunnable, 1000);
        }
    }

    private int getRandomAnswersIndex(){
        return random.nextInt(ANSWERS_TEXT_VIEW_COUNT);
    }

    private String[] getRandomWords(int count, String trueAnswer, int indexTrueAnswer){
        String[] result = new String[count];
        String[] wordsBase = getResources().getStringArray(R.array.speed_reading_words);
        int i = 0;
        while(i < count){
            if(i != indexTrueAnswer) {
                String word = wordsBase[getRandomArrayKey(wordsBase)];
                if ( ! Arrays.asList(result).contains(word) && !word.equals(trueAnswer)) {
                    result[i] = word;
                    i++;
                }
            }
            else{
                result[i] = trueAnswer;
                i++;
            }
        }
        return result;
    }

    private int getRandomArrayKey(String[] array){
        return random.nextInt(array.length);
    }

    AlertDialog dialog;

    private boolean isPaused;
    private boolean isAnswered;

    @Override
    public void onPause() {
        isPaused = true;
        handler.removeCallbacks(checkResultRunnable); //Не уверен нужно ли, но пусть будет

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

                if (isAnswered) {
                    handler.postDelayed(checkResultRunnable, 1000);
                }
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
                getActivity().finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                isPaused = false;
                dialog = null;

                if (isAnswered) {
                    handler.postDelayed(checkResultRunnable, 1000);
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private Runnable checkResultRunnable = new Runnable() {
        @Override
        public void run() {
            if ( ! isPaused) {
                mainActivity.startTrainingFragment();
            }
        }
    };
}
