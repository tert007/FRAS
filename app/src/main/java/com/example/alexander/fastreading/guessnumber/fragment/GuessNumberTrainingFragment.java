package com.example.alexander.fastreading.guessnumber.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.RecordsManager;
import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.Training;
import com.example.alexander.fastreading.guessnumber.GuessNumberActivity;

import java.util.Random;

public class GuessNumberTrainingFragment extends Fragment implements View.OnClickListener, Training {

    private static final String NOT_INITIALIZE_VALUE = "-";

    private static final int COUNT_TRY = 15;
    private static final int COUNT_NUMBER = 10;

    private static final int ANSWERS_TO_COMPLEXITY_DOWN = -2;
    private static final int ANSWERS_TO_COMPLEXITY_UP = 3;

    private int countTrueAnswer = 0;

    private static final int MINIMUM_COMPLEXITY = 4;
    private static final int MAXIMUM_COMPLEXITY = 8;

    private static final int SHOW_NUMBER_DELAY = 1000;

    private int currentResult;
    private int currentComplexity;

    private Random random;

    private int currentCardIndex;
    private int[] trueAnswer;
    private int[] userAnswer;

    private Button[] buttons = new Button[COUNT_NUMBER];
    private Button editButton;
    private TextView[] cards;

    private ProgressBar progressBar;
    private LinearLayout answerLayout;

    private TextView currentResultTextView;
    private TextView recordTextView;

    private TextView pointsTextView;
    private Animation showPointsAnimation;

    private int acceptGreen;
    private int rejectRed;
    private int accentColor;

    private int points;

    private int currentCountTry = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guess_number_main_fragment, null);

        accentColor = ContextCompat.getColor(getActivity(), R.color.colorAccent);
        acceptGreen = ContextCompat.getColor(getActivity(), R.color.accept_green);
        rejectRed =  ContextCompat.getColor(getActivity(), R.color.reject_red);

        currentComplexity = SettingsManager.getGuessNumberComplexity();

        random = new Random();

        progressBar = (ProgressBar) view.findViewById(R.id.guess_number_main_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(accentColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setMax(COUNT_TRY);

        answerLayout = (LinearLayout) view.findViewById(R.id.guess_number_answer_layout);
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

        editButton = (Button) view.findViewById(R.id.guess_number_button_edit);

        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(new ImageSpan(getActivity(), R.drawable.guess_number_edit_button_background), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editButton.setText(spannableString);
        editButton.setOnClickListener(this);
        editButton.setEnabled(false);

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

        handler.postDelayed(preShowNumber, SHOW_RESULT_DELAY);
        cards = createCard(currentComplexity);

        return view;
    }

    @Override
    public void onClick(View v) {
        int currentNumberOfAnswer = -1;

        switch (v.getId()) {
            case R.id.guess_number_button0:
                currentNumberOfAnswer = 0;
                break;
            case R.id.guess_number_button1:
                currentNumberOfAnswer = 1;
                break;
            case R.id.guess_number_button2:
                currentNumberOfAnswer = 2;
                break;
            case R.id.guess_number_button3:
                currentNumberOfAnswer = 3;
                break;
            case R.id.guess_number_button4:
                currentNumberOfAnswer = 4;
                break;
            case R.id.guess_number_button5:
                currentNumberOfAnswer = 5;
                break;
            case R.id.guess_number_button6:
                currentNumberOfAnswer = 6;
                break;
            case R.id.guess_number_button7:
                currentNumberOfAnswer = 7;
                break;
            case R.id.guess_number_button8:
                currentNumberOfAnswer = 8;
                break;
            case R.id.guess_number_button9:
                currentNumberOfAnswer = 9;
                break;
            case R.id.guess_number_button_edit:
                if (currentCardIndex > 0) {
                    currentCardIndex--;
                    cards[currentCardIndex].setText("_");
                }
                return;
        }

        cards[currentCardIndex].setText(String.valueOf(currentNumberOfAnswer));
        userAnswer[currentCardIndex] = currentNumberOfAnswer;

        if (currentCardIndex == currentComplexity - 1) {
            currentCardIndex = 0;
            currentCountTry++;

            progressBar.setProgress(currentCountTry);

            setButtonsEnabled(false);

            boolean itsTrueAnswer = true;
            for (int i = 0; i < currentComplexity; i++) {

                if (userAnswer[i] == trueAnswer[i]) {
                    points++;

                    cards[i].setBackgroundResource(R.drawable.guess_number_card_success_background);
                } else {
                    points--;

                    itsTrueAnswer = false;
                    cards[i].setBackgroundResource(R.drawable.guess_number_card_error_background);
                }

                cards[i].setTextColor(Color.WHITE);
            }

            if (itsTrueAnswer) {
                countTrueAnswer++;
            } else {
                countTrueAnswer--;
            }

            String pointsText;
            if (points >= 0) {
                pointsText = "+" + points;
                pointsTextView.setTextColor(acceptGreen);

            } else {
                pointsText = String.valueOf(points);
                pointsTextView.setTextColor(rejectRed);
            }

            pointsTextView.setText(pointsText);
            pointsTextView.startAnimation(showPointsAnimation);

            currentResult += points;
            if (currentResult < 0) {
                currentResult = 0;
            }
            currentResultTextView.setText(String.valueOf(currentResult));

            if (currentCountTry == COUNT_TRY) {
                isCompleted = true;
                handler.postDelayed(showResult, SHOW_RESULT_DELAY);
            } else {
                handler.postDelayed(preShowNumber, SHOW_NUMBER_DELAY);
            }

        } else {
            currentCardIndex++;
        }
    }

    private boolean isPaused;
    private boolean isCompleted;

    private Dialog dialog;

    @Override
    public void onPause() {
        isPaused = true;

        //По идее не нужно
        handler.removeCallbacks(preShowNumber);
        handler.removeCallbacks(hideHintShowNumber);
        handler.removeCallbacks(showNumber);
        handler.removeCallbacks(postShowNumber);
        handler.removeCallbacks(showResult);

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
        setButtonsEnabled(false);
        currentCardIndex = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);

        builder.setMessage(R.string.training_is_paused);

        builder.setPositiveButton(R.string.dialog_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                isPaused = false;
                dialog = null;

                if (isCompleted) {
                    handler.postDelayed(showResult, SHOW_RESULT_DELAY);
                } else {
                    handler.postDelayed(preShowNumber, SHOW_NUMBER_DELAY);
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

        setButtonsEnabled(false);
        currentCardIndex = 0;

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

                if (isCompleted) {
                    handler.postDelayed(showResult, SHOW_RESULT_DELAY);
                } else {
                    handler.postDelayed(preShowNumber, SHOW_NUMBER_DELAY);
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private static final int SHOW_RESULT_DELAY = 1000;
    private Runnable showResult = new Runnable() {
        @Override
        public void run() {
            if (isPaused) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);

            SettingsManager.setGuessNumberComplexity(currentComplexity);

            if(RecordsManager.getGuessNumberRecord() < currentResult){
                recordTextView.setText(String.valueOf(currentResult));

                RecordsManager.setGuessNumberRecord(currentResult);

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
                    ((GuessNumberActivity) getActivity()).startPrepareFragment();
                }
            });
            builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
    };

    private static final int SHOW_HINT_DELAY = 200;
    private static final int POST_SHOW_DELAY = 500;

    private Handler handler = new Handler();

    private Runnable preShowNumber = new Runnable() {
        @Override
        public void run() {
            if (isPaused) {
                return;
            }

            points = 0;

            setButtonsEnabled(false);

            if (countTrueAnswer == ANSWERS_TO_COMPLEXITY_DOWN) {
                if (currentComplexity > MINIMUM_COMPLEXITY) {
                    currentComplexity--;

                    answerLayout.removeAllViews();
                    cards = createCard(currentComplexity);
                }

                countTrueAnswer = 0;
            }

            if (countTrueAnswer == ANSWERS_TO_COMPLEXITY_UP) {
                if (currentComplexity < MAXIMUM_COMPLEXITY) {
                    currentComplexity++;

                    answerLayout.removeAllViews();
                    cards = createCard(currentComplexity);
                }

                countTrueAnswer = 0;
            }

            for (TextView card : cards) {
                card.setBackgroundResource(R.drawable.guess_number_card_background);
                card.setTextColor(accentColor);
                card.setText("_");
            }

            handler.postDelayed(hideHintShowNumber, SHOW_HINT_DELAY);
        }
    };

    private Runnable hideHintShowNumber = new Runnable() {
        @Override
        public void run() {
            if (isPaused) {
                return;
            }

            for (TextView card : cards) {
                card.setTextColor(Color.BLACK);
            }

            handler.postDelayed(showNumber, SHOW_HINT_DELAY);
        }
    };

    private Runnable showNumber = new Runnable() {
        @Override
        public void run() {
            if (isPaused) {
                return;
            }

            trueAnswer = new int[currentComplexity];
            userAnswer = new int[currentComplexity];

            for (int i = 0; i < currentComplexity; i++) {
                int randomNumber = random.nextInt(COUNT_NUMBER);

                trueAnswer[i] = randomNumber;
                cards[i].setText(String.valueOf(randomNumber));
                cards[i].setTextColor(Color.BLACK);
            }

            handler.postDelayed(postShowNumber, POST_SHOW_DELAY);
        }
    };

    private Runnable postShowNumber = new Runnable() {
        @Override
        public void run() {
            if (isPaused) {
                return;
            }

            for (TextView card : cards) {
                card.setText("_");
            }

            setButtonsEnabled(true);
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

        int textSizeSp = 40;

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

    private void setButtonsEnabled(boolean enabled) {
        for (int i = 0; i < COUNT_NUMBER; i++) {
            buttons[i].setEnabled(enabled);
        }

        editButton.setEnabled(enabled);
    }
}
