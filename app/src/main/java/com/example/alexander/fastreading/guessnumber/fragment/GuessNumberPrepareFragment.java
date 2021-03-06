package com.example.alexander.fastreading.guessnumber.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexander.fastreading.CircularProgressBar;
import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.guessnumber.GuessNumberActivity;

/**
 * Created by Alexander on 27.07.2016.
 */
public class GuessNumberPrepareFragment extends Fragment {

    private static final int SHOW_DELAY = 1000;

    private CircularProgressBar circularProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prepare_fragment, container, false);

        circularProgressBar = (CircularProgressBar) view.findViewById(R.id.shulte_prepare_progress_circular_view);
        circularProgressBar.setProgressColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        circularProgressBar.setMax(SHOW_DELAY);

        return view;
    }


    private int progress = 0;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {


        @Override
        public void run() {
            if (progress < SHOW_DELAY) {
                progress += 50;
                circularProgressBar.setProgress(progress);

                handler.postDelayed(this, 50);
            } else {
                ((GuessNumberActivity) getActivity()).startTrainingFragment();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        handler.post(runnable);
    }

    @Override
    public void onPause() {
        progress = 0;
        handler.removeCallbacks(runnable);

        super.onPause();
    }
}
