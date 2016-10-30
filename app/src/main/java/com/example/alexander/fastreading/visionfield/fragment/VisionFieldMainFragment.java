package com.example.alexander.fastreading.visionfield.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.visionfield.VisionFieldActivity;
import com.example.alexander.fastreading.visionfield.VisionFieldGridAdapter;

import java.util.Random;

/**
 * Created by Alexander on 27.07.2016.
 */
public class VisionFieldMainFragment extends Fragment {

    private final Random random = new Random();

    private static final int DEFAULT_ROW_COUNT = 9;
    private static final int ITEMS_COUNT = 63;

    private static final int START_DELAY = 2000;
    private static final int SHOW_RESULT_DELAY = 2000;
    private int showTimeDelay;

    private int showsCount;
    private int currentShowCount;

    private int mistakesCount;
    private int userFoundMistakes;

    String[] alphabet;

    VisionFieldGridAdapter adapter;
    GridView gridView;
    ProgressBar progressBar;

    private String lastLetter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vision_field_main_fragment, container, false);

        showTimeDelay = SettingsManager.getVisionFieldShowDelay();
        showsCount = SettingsManager.getVisionFieldComplexity() / showTimeDelay;

        alphabet = getResources().getStringArray(R.array.alphabet);

        gridView = (GridView) view.findViewById(R.id.vision_field_main_grid_view);
        gridView.setNumColumns(DEFAULT_ROW_COUNT);

        adapter = new VisionFieldGridAdapter(getActivity(), R.layout.vision_field_main_fragment, getRandomLetters());
        gridView.setAdapter(adapter);

        progressBar = (ProgressBar) view.findViewById(R.id.vision_field_main_progress_bar);
        progressBar.setMax(showsCount);

        Button mistakeButton = (Button) view.findViewById(R.id.vision_field_main_mistake_button);
        mistakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userFoundMistakes++;
            }
        });

        handler.postDelayed(changeLetterRunnable, START_DELAY);

        return view;
    }

    private Handler handler = new Handler();
    private Runnable changeLetterRunnable = new Runnable() {
        @Override
        public void run() {
            String randomLetter = getRandomLetter();
            while (randomLetter.equals(lastLetter)) {
                randomLetter = getRandomLetter();
            }

            lastLetter = randomLetter;

            ((TextView) gridView.getChildAt(0)).setText(randomLetter);
            ((TextView) gridView.getChildAt(4)).setText(randomLetter);
            ((TextView) gridView.getChildAt(8)).setText(randomLetter);

            ((TextView) gridView.getChildAt(27)).setText(randomLetter);
            ((TextView) gridView.getChildAt(35)).setText(randomLetter);

            ((TextView) gridView.getChildAt(54)).setText(randomLetter);
            ((TextView) gridView.getChildAt(58)).setText(randomLetter);
            ((TextView) gridView.getChildAt(62)).setText(randomLetter);

            boolean showDifferentLetter = showDifferentLetter();

            String differentLetter = getRandomLetter();
            while (differentLetter.equals(randomLetter)) {
                differentLetter = getRandomLetter();
            }

            if (showDifferentLetter) {
                ((TextView) gridView.getChildAt(getRandomTextViewIndex())).setText(differentLetter);
                mistakesCount++;
            }

            currentShowCount++;
            progressBar.setProgress(currentShowCount);

            if (currentShowCount < showsCount) {
                handler.postDelayed(this, showTimeDelay);
            } else {
                handler.postDelayed(showResultRunnable, SHOW_RESULT_DELAY);
            }
        }
    };

    private Runnable showResultRunnable = new Runnable() {
        @Override
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);

            String result = null;

            if (userFoundMistakes == mistakesCount) {
                builder.setTitle(R.string.vision_field_main_perfect_result);

                result = getString(R.string.vision_field_main_true_answer) + ": " + mistakesCount;
            } else if (mistakesCount == userFoundMistakes + 1 || mistakesCount == userFoundMistakes - 1) {
                builder.setTitle(R.string.vision_field_main_good_result);

                result = getString(R.string.your_result) + ": " + userFoundMistakes + '\n'
                        + getString(R.string.vision_field_main_true_answer) + ": " + mistakesCount;
            } else {
                builder.setTitle(R.string.training_completed);

                result = getString(R.string.your_result) + ": " + userFoundMistakes + '\n'
                        + getString(R.string.vision_field_main_true_answer) + ": " + mistakesCount;
            }

            builder.setMessage(result);

            builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ((VisionFieldActivity) getActivity()).startTrainingFragment();
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
    };

    private String[] getRandomLetters() {
        String[] result = new String[ITEMS_COUNT];

        for (int i = 0; i < ITEMS_COUNT; i++) {
            result[i] = alphabet[random.nextInt(alphabet.length)];
        }

        return result;
    }

    private String getRandomLetter() {
        return alphabet[random.nextInt(alphabet.length)];
    }

    private int getRandomTextViewIndex() {
        switch (random.nextInt(8)) {
            case 0: return 0;
            case 1: return 4;
            case 2: return 8;
            case 3: return 27;
            case 4: return 35;
            case 5: return 54;
            case 6: return 58;
            default: return 62;
        }
    }

    private boolean showDifferentLetter() {
        switch (random.nextInt(5)) {
            case 0: return false;
            case 1: return false;
            case 2: return false;
            default: return true;
        }
    }



    @Override
    public void onStop() {
        super.onStop();

        handler.removeCallbacks(changeLetterRunnable);
        handler.removeCallbacks(showResultRunnable);
    }
}