package com.example.alexander.fastreading.speedreading.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.speedreading.SpeedReadingActivity;

import java.util.Arrays;
import java.util.Random;

public class SpeedReadingAnswersFragment extends Fragment implements View.OnClickListener {
    private String answerWord;
    private static final int ANSWERS_TEXT_VIEW_COUNT = 6;
    TextView[] textViews;
    int indexTrueAnswer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speed_reading_test_fragment, null);
        textViews = new TextView[ANSWERS_TEXT_VIEW_COUNT];
        textViews[0] = (TextView) view.findViewById(R.id.speed_reading_answer_text_view_1);
        textViews[1] = (TextView) view.findViewById(R.id.speed_reading_answer_text_view_2);
        textViews[2] = (TextView) view.findViewById(R.id.speed_reading_answer_text_view_3);
        textViews[3] = (TextView) view.findViewById(R.id.speed_reading_answer_text_view_4);
        textViews[4] = (TextView) view.findViewById(R.id.speed_reading_answer_text_view_5);
        textViews[5] = (TextView) view.findViewById(R.id.speed_reading_answer_text_view_6);

        answerWord = getArguments().getString("last_word");
        indexTrueAnswer = getRandomAnswersIndex();
        String[] words = getRandomWords(ANSWERS_TEXT_VIEW_COUNT,answerWord,indexTrueAnswer);

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
            case R.id.speed_reading_answer_text_view_1:
                selectedIndex = 0;
                break;
            case R.id.speed_reading_answer_text_view_2:
                selectedIndex = 1;
                break;
            case R.id.speed_reading_answer_text_view_3:
                selectedIndex = 2;
                break;
            case R.id.speed_reading_answer_text_view_4:
                selectedIndex = 3;
                break;
        }
        if(selectedIndex == indexTrueAnswer) {
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context, "Ответ правильный", Toast.LENGTH_LONG).show();
            SpeedReadingActivity mainActivity = (SpeedReadingActivity) getActivity();
            mainActivity.countAnswer++;
            mainActivity.setSpeed(true);
            mainActivity.startExercise();
        }
        else{
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context, "Ответ не правильный", Toast.LENGTH_LONG).show();
            SpeedReadingActivity mainActivity = (SpeedReadingActivity) getActivity();
            mainActivity.countAnswer++;
            mainActivity.setSpeed(false);
            mainActivity.startExercise();
        }
    }

   private int getRandomAnswersIndex(){
        Random random = new Random();
        return random.nextInt(ANSWERS_TEXT_VIEW_COUNT-1);
   }

   private String[] getRandomWords(int count, String trueAnswer, int indexTrueAnswer){
       String[] result = new String[count];
       String[] wordsBase = getResources().getStringArray(R.array.speed_reading_words);
       int i = 0;
       while(i < count){
           if(i != indexTrueAnswer) {
               String word = wordsBase[getRandomArrayKey(wordsBase)];
               if (!Arrays.asList(result).contains(word) && !word.equals(trueAnswer)) {
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
        Random random = new Random();
        return random.nextInt(array.length-1);
    }

}
