package com.example.alexander.fastreading.reader.reader.fragment.reader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 25.09.2016.
 */
public class ReaderFragment extends Fragment {

    public ReaderFragmentOnPauseResponse onPauseDelegate;

    private ProgressDialog progressDialog;

    private TextView textView;
    private TextView speedTextView;
    private TextView speedResultTextView;
    private TextView currentPageTextView;
    private TextView currentPageResultTextView;

    private BookDescription bookDescription;
    private List<CharSequence> bookChapters;

    private int currentPageIndex;
    private List<CharSequence> pages;

    private boolean itsFastReading;
    private boolean fastReadingStarted;
    private WordSelector wordSelector;

    private int pageWidth;
    private int halfPartOfWidth;
    private int thirdPartOfPageWidth;

    private static final int defaultPageChangeDelay = 200;
    private static final int wordLength = 6;

    private int currentSpeedIndex;
    private static final int[] speed = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000, 1050, 1100};

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_fragment, container, false);

        bookDescription = getArguments().getParcelable("book_description");
        //itsFastReading = getArguments().getBoolean("fast_reading");
        bookChapters = getArguments().getCharSequenceArrayList("book");

        textView = (TextView) view.findViewById(R.id.reader_pages_text_view);
        String textSize = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("reader_text_size", getString(R.string.text_size_default_value));
        textView.setTextSize(Integer.valueOf(textSize));

        speedTextView = (TextView) view.findViewById(R.id.reader_pages_speed_text_view);
        speedResultTextView = (TextView) view.findViewById(R.id.reader_pages_speed_result_text_view);

        currentPageTextView = (TextView) view.findViewById(R.id.reader_pages_current_page_text_view);
        currentPageResultTextView = (TextView) view.findViewById(R.id.reader_pages_current_page_result_text_view);

        view.post(new Runnable() {
            @Override
            public void run() {
                pageWidth = textView.getWidth();
                halfPartOfWidth = pageWidth / 2;
                thirdPartOfPageWidth = pageWidth / 3;

                PageSplitter pageSplitter = new PageSplitter(textView.getPaint(), textView.getWidth(), textView.getHeight());
                pages = pageSplitter.getPages(bookChapters);

                setCurrentPage();

                textView.setText(pages.get(currentPageIndex));

                currentPageTextView.setVisibility(View.VISIBLE);
                currentPageResultTextView.setVisibility(View.VISIBLE);
                currentPageResultTextView.setText(getCurrentPageByString());

                showDialog();
            }
        });


        return view;
    }

    private int getBookOffset() {
        int index = 0;
        for (int i = 0; i < currentPageIndex; i++){
            index += pages.get(i).length();
        }

        if (currentPageIndex < pages.size() - 1){
            return index + 1; //??
        }

        return index;
    }

    private void setCurrentPage() {
        currentPageIndex = 0;

        long bookLength = 0;

        if (bookDescription.getBookOffset() < pages.get(0).length()){
            return;
        }

        for (CharSequence page : pages) {
            bookLength += page.length();

            if (bookLength < bookDescription.getBookOffset()){
                currentPageIndex++;
            } else {
                break;
            }
        }
    }

    private String getCurrentPageByString(){
        return String.valueOf(currentPageIndex + 1) + '/' + pages.size();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.reading_type));
        builder.setItems(R.array.reading_type, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                itsFastReading = which != 0;

                if (itsFastReading) {
                    speedTextView.setVisibility(View.VISIBLE);
                    speedResultTextView.setVisibility(View.VISIBLE);
                    speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));

                    textView.setOnTouchListener(fastReadingOnTouchListener);
                    fastReadingStarted = false;
                    wordSelector = new WordSelector(pages.get(currentPageIndex));
                } else {
                    speedTextView.setVisibility(View.GONE);
                    speedResultTextView.setVisibility(View.GONE);

                    textView.setOnTouchListener(onTouchListener);
                }
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Runnable wordSelectorThread = new Runnable() {
        @Override
        public void run() {
            if (fastReadingStarted){
                WorldSelectorPage worldSelectorPage;

                if ((worldSelectorPage = wordSelector.getNextSelectedWord()) != null){
                    textView.setText(worldSelectorPage.getPage());

                    double delay = 60_000 / speed[currentSpeedIndex];
                    //Log.d("LENGTH", String.valueOf(worldSelectorPage.getSelectedWordLength()));
                    //double delay = worldSelectorPage.getSelectedWordLength() * 1000 / (speed[currentSpeedIndex] * wordLength / 60);
                    Log.d("SPEED", String.valueOf(delay));

                    textView.postDelayed(this, (int) delay);
                } else {
                    if (currentPageIndex <  pages.size() - 1){
                        currentPageIndex++;
                        wordSelector = new WordSelector(pages.get(currentPageIndex));

                        textView.postDelayed(this, defaultPageChangeDelay);

                        currentPageResultTextView.setText(getCurrentPageByString());
                    } else {
                        fastReadingStarted = false;
                    }
                }
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();

                if (halfPartOfWidth < x){
                    if (currentPageIndex < pages.size() - 1){
                        currentPageIndex++;
                        textView.setText(pages.get(currentPageIndex));
                        currentPageResultTextView.setText(getCurrentPageByString());
                    }
                } else {
                    if (currentPageIndex > 0){
                        currentPageIndex--;
                        textView.setText(pages.get(currentPageIndex));
                        currentPageResultTextView.setText(getCurrentPageByString());
                    }
                }
            }

            return false;
        }
    };

    private View.OnTouchListener fastReadingOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();

                if (x < thirdPartOfPageWidth) {
                    if (currentSpeedIndex > 0){
                        currentSpeedIndex--;
                        speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));
                    }
                } else if (thirdPartOfPageWidth <= x && x <= pageWidth - thirdPartOfPageWidth){
                    fastReadingStarted = !fastReadingStarted;

                    if (fastReadingStarted) {
                        textView.post(wordSelectorThread);
                    }
                } else {
                    if (currentSpeedIndex < speed.length - 1){
                        currentSpeedIndex++;
                        speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));
                    }
                }
            }

            return false;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        bookDescription.setBookOffset(getBookOffset());
        onPauseDelegate.readerFragmentOnPauseResponse(bookDescription);
    }
}
