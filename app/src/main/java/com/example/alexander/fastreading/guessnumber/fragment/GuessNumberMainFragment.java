package com.example.alexander.fastreading.guessnumber.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.guessnumber.GuessNumberAsyncTask;
import com.example.alexander.fastreading.guessnumber.Response;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Alexander on 31.07.2016.
 *
 *
 * Главный фрагмент GuessActivity
 * Содежржит само упражнение
 */
public class GuessNumberMainFragment extends Fragment implements View.OnClickListener, Response {
    private static final String EMPTY_STRING = "";
    private static final int ANIMATION_REPEAT_COUNT = 3;
    private static final int COUNT_NUMBER = 10;

    private static final int COUNT_TRY = 10;

    Button[] buttons = new Button[COUNT_NUMBER];
    Button editButton;

    TextView showTextView;
    TextView answerTextView;

    ProgressBar progressBar;

    String trueAnswer;
    String userAnswer;

    GuessNumberAsyncTask asyncTask;

    int countTrueAnswer;
    int countTry;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_number_main_fragment, null);

        progressBar = (ProgressBar) view.findViewById(R.id.guess_number_main_progress_bar);

        editButton = (Button) view.findViewById(R.id.guess_number_edit_button);
        editButton.setEnabled(false);
        editButton.setOnClickListener(this);

        buttons[0] = (Button) view.findViewById(R.id.guess_number_button0);
        buttons[1] = (Button) view.findViewById(R.id.guess_number_button1);
        buttons[2] = (Button) view.findViewById(R.id.guess_number_button2);
        buttons[3] = (Button) view.findViewById(R.id.guess_number_button3);
        buttons[4] = (Button) view.findViewById(R.id.guess_number_button4);
        buttons[5] = (Button) view.findViewById(R.id.guess_number_button5);
        buttons[6] = (Button) view.findViewById(R.id.guess_number_button6);
        buttons[7] = (Button) view.findViewById(R.id.guess_number_button7);
        buttons[8] = (Button) view.findViewById(R.id.guess_number_button8);
        buttons[9] = (Button) view.findViewById(R.id.guess_number_button9);

        for (int i = 0; i < COUNT_NUMBER; i++){
            buttons[i].setText(String.valueOf(i));
            buttons[i].setEnabled(false);
            buttons[i].setTextSize(16);

            buttons[i].setOnClickListener(this);
        }

        showTextView = (TextView) view.findViewById(R.id.guess_number_show_text_view);
        answerTextView = (TextView) view.findViewById(R.id.guess_number_answer_text_view);

        userAnswer = EMPTY_STRING;
        trueAnswer = EMPTY_STRING;

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.test);
        animation.setRepeatCount(ANIMATION_REPEAT_COUNT);
        animation.setRepeatMode(Animation.RESTART);

        animation.setAnimationListener(new Animation.AnimationListener() {
            int repeatCount = ANIMATION_REPEAT_COUNT;

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showTextView.setText(EMPTY_STRING);
                showRandomNumber();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                showTextView.setText(String.valueOf(repeatCount--));
            }
        });

        showTextView.setAnimation(animation);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.guess_number_button0:
                userAnswer += "0";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button1:
                userAnswer += "1";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button2:
                userAnswer += "2";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button3:
                userAnswer += "3";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button4:
                userAnswer += "4";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button5:
                userAnswer += "5";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button6:
                userAnswer += "6";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button7:
                userAnswer += "7";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button8:
                userAnswer += "8";
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_button9:
                userAnswer += "9";
                answerTextView.setText(userAnswer);
                answerTextView.setText(userAnswer);
                compare();
                break;
            case R.id.guess_number_edit_button:
                userAnswer = EMPTY_STRING;
                answerTextView.setText(userAnswer);
                compare();
                break;
        }
    }

    @Override
    public void onResponse(String result) {
        showTextView.setText(result);
        trueAnswer = result;

        Timer timer = new Timer();
        timer.schedule(new UpdateTimeTask(), 1000);
    }

    private void showRandomNumber() {
        showTextView.setText(EMPTY_STRING);
        answerTextView.setText(EMPTY_STRING);

        userAnswer = EMPTY_STRING;
        trueAnswer = EMPTY_STRING;

        for (int i = 0; i < COUNT_NUMBER; i++){
            buttons[i].setEnabled(false);
        }
        editButton.setEnabled(false);

        asyncTask = new GuessNumberAsyncTask();
        asyncTask.delegate = this;
        asyncTask.execute(SettingsManager.getGuessNumberComplexity());
    }

    private void compare(){
        if (userAnswer.length() == SettingsManager.getGuessNumberComplexity()) {
            if (userAnswer.equals(trueAnswer)) {
                countTrueAnswer += 1;
                progressBar.setProgress(countTrueAnswer * 10);
                Toast.makeText(getActivity(), "DA", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "NET", Toast.LENGTH_SHORT).show();
            }
            countTry++;
            if (countTry == COUNT_TRY){
                Toast.makeText(getActivity(), "Угадано " + String.valueOf(countTrueAnswer), Toast.LENGTH_LONG).show();
            } else {
                showRandomNumber();
            }
        }
    }

    class UpdateTimeTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showTextView.setText(EMPTY_STRING);
                    for (int i = 0; i < COUNT_NUMBER; i++){
                        buttons[i].setEnabled(true);
                    }

                    editButton.setEnabled(true);
                    showTextView.setEnabled(true);
                }
            });
        }
    }

}
