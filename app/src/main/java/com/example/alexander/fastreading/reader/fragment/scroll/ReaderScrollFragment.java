package com.example.alexander.fastreading.reader.fragment.scroll;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.fragment.scroll.LockableScrollView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alexander on 04.08.2016.
 */
public class ReaderScrollFragment extends Fragment implements View.OnTouchListener {

    private LockableScrollView lockableScrollView;
    private TextView textView;
    private TextView statisticTextView;

    private volatile boolean itsScrolling;

    private static final int MAX_CLICK_DISTANCE = 30;

    private float pressedDownX;
    private float pressedDownY;
    //Запопимаент промежуточный свайп, помогает следить за сложностью
    private float movedOldY;


    private volatile int speed;

    ObjectAnimator animator;
    long length;

    @Override
    public void onPause() {
        super.onPause();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_scroll_fragment, container, false);

        lockableScrollView = (LockableScrollView) view.findViewById(R.id.reader_scroll_fragment_lockable_scroll_view);
        lockableScrollView.setScrollingEnabled(false);

        statisticTextView = (TextView) view.findViewById(R.id.reader_scroll_fragment_speed_text_view);

        textView = (TextView) view.findViewById(R.id.reader_scroll_fragment_text_view);

        String filePath = getArguments().getString("file_path");
        speed = 10;

        statisticTextView.setText(String.valueOf(speed));

        try {
            String textFromFile = FileHelper.getTextFromFile(new File(filePath));
            length = textFromFile.length();
            textView.setText(textFromFile);
            textView.setOnTouchListener(this);
        } catch (IOException e) {
            Log.e("File reading error", e.getMessage());
        }

        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                pressedDownX = event.getX();
                pressedDownY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();

                float dx = pressedDownX - x;
                float dy = pressedDownY - y;

                float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
                float distanceInDp = distanceInPx / getResources().getDisplayMetrics().density;


                if (distanceInDp < MAX_CLICK_DISTANCE){
                    itsScrolling = !itsScrolling;

                    if (animator != null)
                        animator.cancel();

                    if (itsScrolling) {
                        if (!lockableScrollView.isFullScroll()) {
                            animator = new ObjectAnimator();
                            animator.setTarget(lockableScrollView);
                            animator.setIntValues(lockableScrollView.getScrollY(), textView.getBottom());
                            animator.setPropertyName("scrollY");
                            //long duration = ((length - lockableScrollView.getScrollY()) * 100) / speed;
                            animator.setDuration(((length - lockableScrollView.getScrollY()) * 100) / speed);
                            animator.setInterpolator(new LinearInterpolator());
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    itsScrolling = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                            animator.start();

                        }
                    }
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!itsScrolling) {
                    x = event.getX();
                    y = event.getY();

                    dx = pressedDownX - x;
                    dy = pressedDownY - y;

                    distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
                    distanceInDp = distanceInPx / getResources().getDisplayMetrics().density;

                    if (distanceInDp > MAX_CLICK_DISTANCE) {
                        if (dy < movedOldY) {
                            speed++;
                            statisticTextView.setText(String.valueOf(speed));
                        }

                        if (dy > movedOldY) {
                            if (speed > 1){
                                speed--;
                                statisticTextView.setText(String.valueOf(speed));
                            }
                        }
                    }

                    movedOldY = dy;
                }
                return false;

        }
        return false;
    }
}
