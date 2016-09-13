package com.example.alexander.fastreading.reader.fragment.scroll;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;
import com.example.alexander.fastreading.reader.entity.BookDescription;

/**
 * Created by Alexander on 04.08.2016.
 */
public class ReaderScrollFragment extends Fragment implements ScrollFileReadingAsyncTaskResponse {

    private LockableScrollView lockableScrollView;
    private TextView textView;

    private TextView speedTextView;
    private TextView speedResultTextView;

    private TextView progressTextView;
    private TextView progressResultTextView;

    private boolean itsFastReading;
    private BookDescription bookDescription;

    private volatile boolean itsScrolling;
    private volatile int speed = 10;

    private static final int MAX_CLICK_DISTANCE = 50;

    private float pressedDownX;
    private float pressedDownY;
    //Запопимаент промежуточный свайп, помогает следить за сложностью
    private float movedOldY;

    private ObjectAnimator animator;
    long length;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_scroll_fragment, container, false);

        lockableScrollView = (LockableScrollView) view.findViewById(R.id.reader_scroll_lockable_scroll_view);

        speedTextView = (TextView) view.findViewById(R.id.reader_scroll_speed_text_view);
        speedResultTextView = (TextView) view.findViewById(R.id.reader_scroll_speed_result_text_view);

        progressTextView = (TextView) view.findViewById(R.id.reader_scroll_progress_text_view);
        progressResultTextView = (TextView) view.findViewById(R.id.reader_scroll_progress_result_text_view);

        textView = (TextView) view.findViewById(R.id.reader_scroll_text_view);
        textView.setTextSize(SettingsManager.getReaderTextSize());

        bookDescription = (BookDescription) getArguments().getParcelable("book_description");
        itsFastReading = getArguments().getInt("fast_reading") == 1;

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(bookDescription.getTitle());

        ScrollFileReadingAsyncTask asyncTask;

        asyncTask = new ScrollFileReadingAsyncTask(getActivity());
        asyncTask.delegate = this;
        asyncTask.execute(bookDescription);

        return view;
    }

    private int getBookOffset() {
        int lineIndex = textView.getLayout().getLineForVertical(lockableScrollView.getScrollY());
        return textView.getLayout().getLineStart(lineIndex);
    }

    private void setBookOffset() {
        int lineForOffset = textView.getLayout().getLineForOffset(bookDescription.getBookOffset());
        int vertical = textView.getLayout().getLineTop(lineForOffset);

        lockableScrollView.scrollTo(0, vertical);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (animator != null) {
            animator.cancel();
        }

        bookDescription.setBookOffset(getBookOffset());

        BookDescriptionDaoFactory daoFactory = BookDescriptionDaoFactory.getDaoFactory(getActivity());
        BookDescriptionDao bookDescriptionDao = daoFactory.getBookDescriptionDao();

        bookDescriptionDao.updateBookDescription(bookDescription);
    }

    @Override
    public void fileReadingPostExecute(CharSequence text) {
        if (text == null){
            /////FIX
            Toast.makeText(getActivity(), "File reading error", Toast.LENGTH_SHORT).show();
        } else {
            textView.setText(text);

            lockableScrollView.setScrollingEnabled(!itsFastReading);

            progressTextView.setVisibility(View.VISIBLE);
            progressResultTextView.setVisibility(View.VISIBLE);

            if (itsFastReading) {
                speedTextView.setVisibility(View.VISIBLE);
                speedResultTextView.setVisibility(View.VISIBLE);

                textView.setOnTouchListener(onTouchListener);
            }

            length = text.length(); // fix


            textView.post(new Runnable() {
                @Override
                public void run() {
                    setBookOffset();
                }
            });
        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
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
                                //speedTextView.setText(String.valueOf(speed));
                            }

                            if (dy > movedOldY) {
                                if (speed > 1){
                                    speed--;
                                    //speedTextView.setText(String.valueOf(speed));
                                }
                            }
                        }

                        movedOldY = dy;
                    }
                    return false;

            }
            return false;
        }
    };
}
