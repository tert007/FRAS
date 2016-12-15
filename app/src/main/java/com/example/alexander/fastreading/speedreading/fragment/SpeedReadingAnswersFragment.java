package com.example.alexander.fastreading.speedreading.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.speedreading.SpeedReadingActivity;

import java.util.Arrays;
import java.util.Random;

public class SpeedReadingAnswersFragment extends Fragment implements View.OnClickListener {
    private String answerWord;

    private static final Random random = new Random();

    private static final int ANSWERS_TEXT_VIEW_COUNT = 6;
    Button[] textViews;
    TextView speedTextView;
    SpeedReadingActivity mainActivity;
    int indexTrueAnswer;

    private int acceptGreen;
    private int rejectRed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        acceptGreen = ContextCompat.getColor(getActivity(), R.color.accept_green);
        rejectRed  = ContextCompat.getColor(getActivity(), R.color.reject_red);

        View view = inflater.inflate(R.layout.speed_reading_answer_fragment, null);

        textViews = new Button[ANSWERS_TEXT_VIEW_COUNT];

        textViews[0] = (Button) view.findViewById(R.id.speed_reading_answer_button_1);
        textViews[1] = (Button) view.findViewById(R.id.speed_reading_answer_button_2);
        textViews[2] = (Button) view.findViewById(R.id.speed_reading_answer_button_3);
        textViews[3] = (Button) view.findViewById(R.id.speed_reading_answer_button_4);
        textViews[4] = (Button) view.findViewById(R.id.speed_reading_answer_button_5);
        textViews[5] = (Button) view.findViewById(R.id.speed_reading_answer_button_6);

        speedTextView = (TextView) view.findViewById(R.id.speed_reading_answer_speed_text_view);

        mainActivity = (SpeedReadingActivity) getActivity();

        answerWord = getArguments().getString("true_answer");

        speedTextView.setText(String.valueOf(mainActivity.wordsPerMinute[mainActivity.index]));
        indexTrueAnswer = getRandomAnswersIndex();
        String[] words = getRandomWords(ANSWERS_TEXT_VIEW_COUNT, answerWord, indexTrueAnswer);

        for (int i = 0; i < textViews.length; i++){
            textViews[i].setText(words[i]);
            textViews[i].setOnClickListener(this);
        }
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

        if(selectedIndex == indexTrueAnswer) {
            mainActivity.countAnswer++;
            mainActivity.setSpeed(true);
            speedTextView.setText(String.valueOf(mainActivity.wordsPerMinute[mainActivity.index]));
            speedTextView.setTextColor(acceptGreen);
            Handler handler = new Handler();
            handler.postDelayed(speedDelayed,1000);
        }
        else{
            mainActivity.countAnswer++;
            mainActivity.setSpeed(false);
            speedTextView.setText(String.valueOf(mainActivity.wordsPerMinute[mainActivity.index]));
            speedTextView.setTextColor(rejectRed);
            Handler handler = new Handler();
            handler.postDelayed(speedDelayed,1000);
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

    private Runnable speedDelayed = new Runnable() {
        @Override
        public void run() {
            mainActivity.startTrainingFragment();
        }
    };
}
