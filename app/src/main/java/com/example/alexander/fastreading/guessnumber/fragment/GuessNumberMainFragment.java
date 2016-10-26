package com.example.alexander.fastreading.guessnumber.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.RecordsManager;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.guessnumber.GuessNumberActivity;

import java.util.Random;

/**
 * Created by Alexander on 31.07.2016.
 * <p/>
 * <p/>
 * Главный фрагмент GuessActivity
 * Содежржит само упражнение
 */
public class GuessNumberMainFragment extends Fragment implements View.OnClickListener {

    private static final String NOT_INITIALIZE_VALUE = "-";

    private static final int COUNT_TRY = 15;
    private static final int COUNT_NUMBER = 10;

    private static final int ANSWER_TO_DOWN = -2;
    private static final int ANSWER_TO_UP = 3;

    private int countTrueAnswer = 0;

    private static final int MINIMUM_COMPLEXITY = 4;
    private static final int MAXIMUM_COMPLEXITY = 8;

    private static final int SHOW_NUMBER_PERIOD = 1000;

    private int currentResult;
    private int currentComplexity;

    private Random random;

    private int currentCardIndex;
    private int[] trueAnswer;

    private TextView[] buttons = new TextView[COUNT_NUMBER];
    private TextView[] cards;

    private ProgressBar progressBar;
    private LinearLayout answerLayout;

    private TextView currentResultTextView;
    private TextView recordTextView;
    private TextView pointsTextView;

    Animation showPointsAnimation;

    private int currentCountTry = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_number_main_fragment, null);

        currentComplexity = SettingsManager.getGuessNumberComplexity();

        random = new Random();

        progressBar = (ProgressBar) view.findViewById(R.id.guess_number_main_progress_bar);
        progressBar.setMax(COUNT_TRY);

        answerLayout = (LinearLayout) view.findViewById(R.id.guess_number_answer_layout);
        buttons[0] = (TextView) view.findViewById(R.id.guess_number_button0);
        buttons[1] = (TextView) view.findViewById(R.id.guess_number_button1);
        buttons[2] = (TextView) view.findViewById(R.id.guess_number_button2);
        buttons[3] = (TextView) view.findViewById(R.id.guess_number_button3);
        buttons[4] = (TextView) view.findViewById(R.id.guess_number_button4);
        buttons[5] = (TextView) view.findViewById(R.id.guess_number_button5);
        buttons[6] = (TextView) view.findViewById(R.id.guess_number_button6);
        buttons[7] = (TextView) view.findViewById(R.id.guess_number_button7);
        buttons[8] = (TextView) view.findViewById(R.id.guess_number_button8);
        buttons[9] = (TextView) view.findViewById(R.id.guess_number_button9);

        currentResultTextView = (TextView) view.findViewById(R.id.guess_number_current_result);
        currentResultTextView.setText(String.valueOf(currentResult));

        recordTextView = (TextView) view.findViewById(R.id.guess_number_record);
        int record = RecordsManager.getGuessNumberRecord();
        if (record == 0) {
            recordTextView.setText(NOT_INITIALIZE_VALUE);
        } else {
            recordTextView.setText(String.valueOf(record));
        }

        pointsTextView = (TextView) view.findViewById(R.id.guess_number_points);
        showPointsAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.guess_number_show_point);
        showPointsAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                pointsTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                pointsTextView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        for (TextView button : buttons) {
            button.setEnabled(false);
            button.setOnClickListener(this);
        }

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.guess_number_main_start_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(preShowNumber, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cards = createCard(currentComplexity);
        answerLayout.setAnimation(animation);

        return view;
    }

    @Override
    public void onClick(View v) {
        int currentNumberOfAnswer = -1;

        switch (v.getId()) {
            case R.id.guess_number_button0:
                cards[currentCardIndex].setText("0");
                currentNumberOfAnswer = 0;
                break;
            case R.id.guess_number_button1:
                cards[currentCardIndex].setText("1");
                currentNumberOfAnswer = 1;
                break;
            case R.id.guess_number_button2:
                cards[currentCardIndex].setText("2");
                currentNumberOfAnswer = 2;
                break;
            case R.id.guess_number_button3:
                cards[currentCardIndex].setText("3");
                currentNumberOfAnswer = 3;
                break;
            case R.id.guess_number_button4:
                cards[currentCardIndex].setText("4");
                currentNumberOfAnswer = 4;
                break;
            case R.id.guess_number_button5:
                cards[currentCardIndex].setText("5");
                currentNumberOfAnswer = 5;
                break;
            case R.id.guess_number_button6:
                cards[currentCardIndex].setText("6");
                currentNumberOfAnswer = 6;
                break;
            case R.id.guess_number_button7:
                cards[currentCardIndex].setText("7");
                currentNumberOfAnswer = 7;
                break;
            case R.id.guess_number_button8:
                cards[currentCardIndex].setText("8");
                currentNumberOfAnswer = 8;
                break;
            case R.id.guess_number_button9:
                cards[currentCardIndex].setText("9");
                currentNumberOfAnswer = 9;
                break;
        }

        if (currentNumberOfAnswer != trueAnswer[currentCardIndex]) {
            for (int i = 0; i < COUNT_NUMBER; i++) {
                buttons[i].setEnabled(false);
            }

            currentCountTry++;
            countTrueAnswer--;

            progressBar.setProgress(currentCountTry);

            cards[currentCardIndex].setBackgroundResource(R.drawable.guess_number_card_error_background);
            cards[currentCardIndex].setTextColor(Color.WHITE);

            if (countTrueAnswer == ANSWER_TO_DOWN) {
                if (currentComplexity > MINIMUM_COMPLEXITY) {
                    currentComplexity--;

                    answerLayout.removeAllViews();
                    cards = createCard(currentComplexity);
                }

                countTrueAnswer = 0;
            }

            handler.postDelayed(preShowNumber, SHOW_NUMBER_PERIOD);
        } else {
            cards[currentCardIndex].setBackgroundResource(R.drawable.guess_number_card_success_background);
            cards[currentCardIndex].setTextColor(Color.WHITE);

            if (currentCardIndex == cards.length - 1) {
                for (int i = 0; i < COUNT_NUMBER; i++) {
                    buttons[i].setEnabled(false);
                }

                currentCountTry++;
                countTrueAnswer++;

                currentResult += currentComplexity;
                currentResultTextView.setText(String.valueOf(currentResult));

                progressBar.setProgress(currentCountTry);

                if (countTrueAnswer == ANSWER_TO_UP) {
                    if (currentComplexity < MAXIMUM_COMPLEXITY) {
                        currentComplexity++;

                        answerLayout.removeAllViews();
                        cards = createCard(currentComplexity);
                    }

                    countTrueAnswer = 0;
                }

                String pointsText = "+" + currentComplexity;
                pointsTextView.setText(pointsText);
                pointsTextView.startAnimation(showPointsAnimation);


                handler.postDelayed(preShowNumber, SHOW_NUMBER_PERIOD);
            }
        }

        if (currentCountTry == COUNT_TRY) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);

            if(RecordsManager.getGuessNumberRecord() < currentResult){
                recordTextView.setText(String.valueOf(currentResult));

                RecordsManager.setGuessNumberRecord(currentResult);
                //new recordTextView
                builder.setTitle(R.string.new_record);
                builder.setMessage(getString(R.string.new_record) + ": " + currentResult);
            } else {
                builder.setTitle(R.string.training_completed);
                int record = RecordsManager.getGuessNumberRecord();
                if (record == 0) {
                    builder.setMessage(getString(R.string.your_result) + ": " + currentResult + "\n" + getString(R.string.record) + ": " + NOT_INITIALIZE_VALUE);
                } else {
                    builder.setMessage(getString(R.string.your_result) + ": " + currentResult + "\n" + getString(R.string.record) + ": " + record);
                }
            }

            builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ((GuessNumberActivity) getActivity()).startTrainingFragment();
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

        if (currentCardIndex < cards.length - 1) {
            currentCardIndex++;
        }
    }

    private static final int SHOW_HINT_DELAY = 200;
    private static final int SHOW_TIME = 500;

    private Handler handler = new Handler();
    private Runnable preShowNumber = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < COUNT_NUMBER; i++) {
                buttons[i].setEnabled(false);
            }

            currentCardIndex = 0;

            for (TextView card : cards) {
                card.setBackgroundResource(R.drawable.guess_number_card_background);
                card.setTextColor(Color.parseColor("#FFA726"));
                card.setText("_");
            }

            handler.postDelayed(hideHintShowNumber, SHOW_HINT_DELAY);
        }
    };
    private Runnable hideHintShowNumber = new Runnable() {
        @Override
        public void run() {

            for (TextView card : cards) {
                card.setTextColor(Color.BLACK);
            }

            handler.postDelayed(showNumber, SHOW_HINT_DELAY);
        }
    };
    private Runnable showNumber = new Runnable() {
        @Override
        public void run() {

            trueAnswer = new int[currentComplexity];
            for (int i = 0; i < currentComplexity; i++) {
                int randomNumber = random.nextInt(COUNT_NUMBER);

                trueAnswer[i] = randomNumber;
                cards[i].setText(String.valueOf(randomNumber));
                cards[i].setTextColor(Color.BLACK);
            }

            handler.postDelayed(postShowNumber, SHOW_TIME);
        }
    };
    private Runnable postShowNumber = new Runnable() {
        @Override
        public void run() {
            for (TextView card : cards) {
                card.setText("_");
            }

            for (int i = 0; i < COUNT_NUMBER; i++) {
                buttons[i].setEnabled(true);
            }
        }
    };

    private TextView[] createCard(int count) {
        TextView[] cards = new TextView[count];
        float scale = getResources().getDisplayMetrics().density;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(4, 0, 4, 0);
        layoutParams.gravity = Gravity.CENTER;

        int paddingInDp = 4;
        int paddingInPx = (int) (paddingInDp * scale + 0.5f);
        int textSizeSp = getTextSize(count);


        for (int i = 0; i < count; i++) {
            cards[i] = new TextView(getActivity());
            cards[i].setLayoutParams(layoutParams);
            cards[i].setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

            cards[i].setBackgroundResource(R.drawable.guess_number_card_background);
            cards[i].setText("_");
            cards[i].setTextSize(textSizeSp);
            cards[i].setTextColor(Color.BLACK);
            answerLayout.addView(cards[i]);
        }
        return cards;
    }

    private int getTextSize(int count) {
        int defaultTextSizeDp = 100 / count;
        float scale = getResources().getDisplayMetrics().density;
        return (int) (defaultTextSizeDp * scale);
    }

    @Override
    public void onStop() {
        super.onStop();

        SettingsManager.setGuessNumberComplexity(currentComplexity);
    }
}
