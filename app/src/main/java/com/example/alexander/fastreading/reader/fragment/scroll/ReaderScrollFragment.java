package com.example.alexander.fastreading.reader.fragment.scroll;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;

/**
 * Created by Alexander on 04.08.2016.
 */
public class ReaderScrollFragment extends Fragment implements  View.OnTouchListener, ScrollFileReadingAsyncTaskResponse {

    private LockableScrollView lockableScrollView;
    private TextView textView;
    private TextView statisticTextView;

    private boolean itsFastReading;

    private Menu menu;

    private volatile boolean itsScrolling;
    private volatile int speed = 10;

    private static final int MAX_CLICK_DISTANCE = 50;

    private float pressedDownX;
    private float pressedDownY;
    //Запопимаент промежуточный свайп, помогает следить за сложностью
    private float movedOldY;

    private ObjectAnimator animator;
    long length;

    private View view;

    private ScrollFileReadingAsyncTask asyncTask;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_scroll_fragment, container, false);

        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //setHasOptionsMenu(true);


        lockableScrollView = (LockableScrollView) view.findViewById(R.id.reader_scroll_fragment_lockable_scroll_view);
        lockableScrollView.setScrollingEnabled(true);

        statisticTextView = (TextView) view.findViewById(R.id.reader_scroll_fragment_speed_text_view);
        //statisticTextView.setText(String.valueOf(speed));

        textView = (TextView) view.findViewById(R.id.reader_scroll_fragment_text_view);
        textView.setTextSize(SettingsManager.getReaderTextSize());

        String filePath = getArguments().getString("file_path");

        long id = getArguments().getLong("book_id");

        asyncTask = new ScrollFileReadingAsyncTask(getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(id);

        this.view = view;

        return view;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fast_reading :
                if (lockableScrollView.isFullScroll()){
                    item.setIcon(R.drawable.fast_reading_start);

                    Snackbar snackbar = Snackbar.make(view, "Reading complete", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return true;
                }

                itsFastReading = !itsFastReading;

                if (itsFastReading) {
                    item.setIcon(R.drawable.fast_reading_stop);

                    Snackbar snackbar = Snackbar.make(view, "Click to screen to start", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    statisticTextView.setText(String.valueOf(speed));

                    textView.setOnTouchListener(this);
                    lockableScrollView.setScrollingEnabled(false);
                } else {
                    item.setIcon(R.drawable.fast_reading_start);

                    statisticTextView.setVisibility(View.GONE);

                    textView.setOnTouchListener(null);
                    lockableScrollView.setScrollingEnabled(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reader_toolbar, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu,inflater);
    }
    */

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

                                    if (lockableScrollView.isFullScroll()) {
                                        itsFastReading = false;
                                    }


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

    @Override
    public void onPause() {
        super.onPause();
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    public void fileReadingPostExecute(CharSequence text) {
        if (text == null){
            /////FIX
            Toast.makeText(getActivity(), "File reading error", Toast.LENGTH_SHORT).show();
        } else {
            textView.setText(text);
            length = text.length();
        }
    }
}
