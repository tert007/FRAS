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
import com.example.alexander.fastreading.guessnumber.GuessNumberAsyncTask;
import com.example.alexander.fastreading.guessnumber.Response;

/**
 * Created by Alexander on 31.07.2016.
 * <p/>
 * <p/>
 * Главный фрагмент GuessActivity
 * Содежржит само упражнение
 */
public class GuessNumberMainFragment extends Fragment implements View.OnClickListener, Response {

    private static final int COUNT_TRY = 10;
    private static final int COUNT_NUMBER = 10;

    private int currentCardIndex = 0;

    private int[] trueAnswer;


    private TextView[] buttons = new TextView[COUNT_NUMBER];
    private TextView[] cards;


    private ProgressBar progressBar;
    private LinearLayout answerLayout;

    private TextView currentResult;
    private TextView recordTextView;
    private TextView points;

    private GuessNumberAsyncTask asyncTask;

    private int countTrueAnswer = 0;
    private int countTry = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_number_main_fragment, null);

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

        points = (TextView) view.findViewById(R.id.guess_number_points);
        currentResult = (TextView) view.findViewById(R.id.guess_number_current_result);

        recordTextView = (TextView) view.findViewById(R.id.guess_number_record);
        recordTextView.setText(String.valueOf(RecordsManager.getGuessNumberRecord()));

        for (TextView button : buttons) {
            button.setEnabled(false);
            button.setOnClickListener(this);
        }

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.test);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showRandomNumber();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cards = addCardsForAnswer(SettingsManager.getGuessNumberComplexity());
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

        cards[currentCardIndex].setTextColor(Color.WHITE);

        if (currentNumberOfAnswer != trueAnswer[currentCardIndex]) {
            for (TextView button : buttons) {
                button.setEnabled(false);
            }

            countTry++;
            progressBar.setProgress(countTry);
            //progressBar.setProgress(countTry * 10);

            cards[currentCardIndex].setBackgroundResource(R.drawable.guess_number_card_error_background);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRandomNumber();
                }
            }, 1000);
        } else {
            cards[currentCardIndex].setBackgroundResource(R.drawable.guess_number_card_success_background);
            if (currentCardIndex == cards.length - 1) {
                countTry++;
                countTrueAnswer++;

                progressBar.setProgress(countTry);

                for (TextView button : buttons) {
                    button.setEnabled(false);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showRandomNumber();
                    }
                }, 1000);
            }
        }

        if (countTry == COUNT_TRY) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);

            int points = countTrueAnswer * SettingsManager.getGuessNumberComplexity();

            if(RecordsManager.getGuessNumberRecord() < points){
                recordTextView.setText(String.valueOf(points));

                RecordsManager.setGuessNumberRecord(points);
                //new recordTextView
                builder.setTitle(R.string.new_record);
                builder.setMessage(getString(R.string.new_record) + ": " + points);
            } else {
                builder.setTitle(R.string.training_completed);
                builder.setMessage(getString(R.string.your_result) + ": " + points + "\n" + getString(R.string.record) + ": " + RecordsManager.getGuessNumberRecord());
            }

            builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GuessNumberActivity mainActivity = (GuessNumberActivity) getActivity();
                    mainActivity.restartGame();
                }
            });
            builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GuessNumberActivity mainActivity = (GuessNumberActivity) getActivity();
                    mainActivity.startSettingsFragment();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (currentCardIndex < cards.length - 1) {
            currentCardIndex++;
        }
    }

    @Override
    public void onResponse(int[] result) {
        trueAnswer = result;
        View view = getView();
        if (view != null) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < COUNT_NUMBER; i++) {
                        buttons[i].setEnabled(true);
                    }
                    for (int i = 0; i < cards.length; i++) {
                        cards[i].setText("_");
                    }
                }
            }, 1000);
        }
        for (int i = 0; i < trueAnswer.length; i++) {
            cards[i].setText(String.valueOf(trueAnswer[i]));
        }
    }

    private void showRandomNumber() {
        for (TextView card : cards) {
            card.setBackgroundResource(R.drawable.guess_number_card_background);
            card.setTextColor(Color.BLACK);
        }
        currentCardIndex = 0;

        asyncTask = new GuessNumberAsyncTask();
        asyncTask.delegate = this;
        asyncTask.execute(SettingsManager.getGuessNumberComplexity());
    }

    private TextView[] addCardsForAnswer(int count) {
        TextView[] cards = new TextView[count];
        float scale = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        for (int i = 0; i < count; i++) {
            if (i != 0) {
                layoutParams.setMargins(5, 0, 5, 0);
            }
            cards[i] = new TextView(getActivity());
            cards[i].setLayoutParams(layoutParams);
            int paddingInDp = 6;
            int paddingInPx = (int) (paddingInDp * scale + 0.5f);
            cards[i].setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
            //this.setTextAppearance(cards[i], context, android.R.style.TextAppearance_Large);
            cards[i].setBackgroundResource(R.drawable.guess_number_card_background);
            cards[i].setText("_");
            cards[i].setTextSize(getNehaiCoefficent(SettingsManager.getGuessNumberComplexity()));
            cards[i].setTextColor(Color.BLACK);
            answerLayout.addView(cards[i]);
        }
        return cards;
    }

    /*
    private void setTextAppearance(TextView textView, Context context, int resId) {

        if (Build.VERSION.SDK_INT < 23) {

            textView.setTextAppearance(context, resId);

        } else {

            textView.setTextAppearance(resId);
        }
    }
*/
    private int getNehaiCoefficent(int count) {
        int marginInDp = 120;
        float scale = getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * scale + 0.5f);
        return marginInPx / count;
    }

}
