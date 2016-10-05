package com.example.alexander.fastreading.reader.reader.fragment.reader;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.dao.BookController;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.reader.Finish;

import java.util.List;

/**
 * Created by Alexander on 25.09.2016.
 */
public class ReaderFragment extends Fragment {

    private BookController bookController;

    public ReaderFragmentOnPauseResponse onPauseDelegate;

    private TextView textView;
    private TextView speedTextView;
    private TextView speedResultTextView;
    private TextView currentPageTextView;
    private TextView currentPageResultTextView;
    private AppCompatSeekBar seekBar;
    private TextView currentPageSeekTextView;
    private TextView currentChapterTextView;
    private View navigationLayout;

    private BookDescription bookDescription;
    private List<CharSequence> bookChapters;
    private List<String> titles;

    private int currentPageIndex;
    private SeparatedBook separatedBook;
    //private List<CharSequence> pages;

    private boolean itsSeekPause = true; // Seek Bar start status

    private boolean itsFastReading;
    private boolean fastReadingStarted;
    private WordSelector wordSelector;

    private int pageWidth;
    private int thirdPartOfPageWidth;

    private static final int defaultPageChangeDelay = 200;
    private static final int wordLength = 6;

    private int currentSpeedIndex;
    private static final int[] speed = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000, 1050, 1100, 1150, 1200, 1250, 1300, 1350, 1400, 1450, 1500};

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_fragment, container, false);

        bookController = new BookController(getActivity());

        bookDescription = getArguments().getParcelable("book_description");
        bookChapters = getArguments().getCharSequenceArrayList("book");
        titles = getArguments().getStringArrayList("title_list");

        textView = (TextView) view.findViewById(R.id.reader_pages_text_view);
        String textSize = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("reader_text_size", getString(R.string.text_size_default_value));
        textView.setTextSize(Integer.valueOf(textSize));

        speedTextView = (TextView) view.findViewById(R.id.reader_pages_speed_text_view);
        speedResultTextView = (TextView) view.findViewById(R.id.reader_pages_speed_result_text_view);

        currentPageTextView = (TextView) view.findViewById(R.id.reader_pages_current_page_text_view);
        currentPageResultTextView = (TextView) view.findViewById(R.id.reader_pages_current_page_result_text_view);

        navigationLayout = view.findViewById(R.id.reader_pages_navigation_layout);
        currentChapterTextView = (TextView) view.findViewById(R.id.reader_pages_current_chapter_title_text_view);
        currentPageSeekTextView = (TextView) view.findViewById(R.id.reader_pages_seek_text_view);
        seekBar = (AppCompatSeekBar) view.findViewById(R.id.reader_pages_seek_bar);

        textView.post(new Runnable() {
            @Override
            public void run() {
                pageWidth = textView.getWidth();
                thirdPartOfPageWidth = pageWidth / 3;

                PageSplitter pageSplitter = new PageSplitter(textView.getPaint(), textView.getWidth(), textView.getHeight());
                separatedBook = pageSplitter.getSeparatedBook(titles, bookChapters);

                setCurrentPage();

                textView.setText(separatedBook.getPage((currentPageIndex)));

                currentPageTextView.setVisibility(View.VISIBLE);
                currentPageResultTextView.setVisibility(View.VISIBLE);
                currentPageResultTextView.setText(getCurrentPageByString());

                speedTextView.setVisibility(View.GONE);
                speedResultTextView.setVisibility(View.GONE);

                textView.setOnTouchListener(onTouchListener);
                seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

                ((Finish)getActivity()).onFinish(); // KOSTIL??!

                showDialog();
            }
        });

        return view;
    }

    private int getBookOffset() {
        int index = 0;
        for (int i = 0; i < currentPageIndex; i++){
            index += separatedBook.getPage(i).length();
        }

        if (currentPageIndex < separatedBook.size() - 1){
            return index + 1; //??
        }

        return index;
    }

    private void setCurrentPage() {
        currentPageIndex = 0;

        long bookLength = 0;

        if (bookDescription.getBookOffset() <= separatedBook.getPage(0).length()){
            return;
        }



        for (int i = 0; i < separatedBook.size(); i++) {
            bookLength += separatedBook.getPage(i).length();

            if (bookLength <= bookDescription.getBookOffset()){
                currentPageIndex++;
            } else {
                break;
            }
        }
    }

    private String getCurrentPageByString(){
        return String.valueOf(currentPageIndex + 1) + '/' + separatedBook.size();
    }

    private void showDialog() {
        final boolean itWasFastReading = itsFastReading;

        fastReadingStarted = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.reading_type));

        int index = 0;
        if (itsFastReading) {
            index = 1;
        }

        builder.setSingleChoiceItems(R.array.reading_type, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itsFastReading = i != 0;

                if (itsFastReading) {
                    speedTextView.setVisibility(View.VISIBLE);
                    speedResultTextView.setVisibility(View.VISIBLE);
                    speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));

                    textView.setOnTouchListener(fastReadingOnTouchListener);
                    seekBar.setOnSeekBarChangeListener(fastReadingSeekBarChangeListener);

                    if (!itWasFastReading) {
                        wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
                    }
                } else {
                    speedTextView.setVisibility(View.GONE);
                    speedResultTextView.setVisibility(View.GONE);

                    textView.setText(separatedBook.getPage(currentPageIndex));

                    textView.setOnTouchListener(onTouchListener);
                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showReadingDialog() {
        showDialog();
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

                    textView.postDelayed(this, (int) delay);
                } else {
                    if (currentPageIndex <  separatedBook.size() - 1){
                        currentPageIndex++;
                        wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));

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

                if (x < thirdPartOfPageWidth) {
                    itsSeekPause = true;
                    navigationLayout.setVisibility(View.GONE);

                    if (currentPageIndex > 0){
                        currentPageIndex--;
                        textView.setText(separatedBook.getPage(currentPageIndex));
                        currentPageResultTextView.setText(getCurrentPageByString());
                    }
                } else if (thirdPartOfPageWidth <= x && x <= pageWidth - thirdPartOfPageWidth){

                    itsSeekPause = !itsSeekPause;

                    if (!itsSeekPause) {
                        currentChapterTextView.setText(separatedBook.getTitle(currentPageIndex));
                        seekBar.setMax(separatedBook.size() - 1);
                        seekBar.setProgress(currentPageIndex);
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        navigationLayout.setVisibility(View.VISIBLE);

                    } else {
                        navigationLayout.setVisibility(View.GONE);

                    }
                } else {
                    itsSeekPause = true;
                    navigationLayout.setVisibility(View.GONE);

                    if (currentPageIndex < separatedBook.size() - 1){
                        currentPageIndex++;
                        textView.setText(separatedBook.getPage(currentPageIndex));
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
                    //itsSeekPause = !itsSeekPause;

                    fastReadingStarted = !fastReadingStarted;

                    if (fastReadingStarted) {
                        navigationLayout.setVisibility(View.GONE);

                        textView.post(wordSelectorThread);
                    } else {
                        currentChapterTextView.setText(separatedBook.getTitle(currentPageIndex));
                        seekBar.setMax(separatedBook.size() - 1);
                        seekBar.setProgress(currentPageIndex);
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        navigationLayout.setVisibility(View.VISIBLE);
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

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                currentPageIndex = progress;
                currentPageSeekTextView.setText(getCurrentPageByString());
                currentChapterTextView.setText(separatedBook.getTitle(currentPageIndex));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            textView.setText(separatedBook.getPage(currentPageIndex));
            currentPageResultTextView.setText(getCurrentPageByString());
        }
    };

    private SeekBar.OnSeekBarChangeListener fastReadingSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                currentPageIndex = progress;
                currentPageSeekTextView.setText(getCurrentPageByString());
                currentChapterTextView.setText(separatedBook.getTitle(currentPageIndex));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            textView.setText(separatedBook.getPage(currentPageIndex));
            wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
            currentPageResultTextView.setText(getCurrentPageByString());
        }
    };


    @Override
    public void onPause() {
        super.onPause();

        bookDescription.setBookOffset(getBookOffset());
        onPauseDelegate.readerFragmentOnPauseResponse(bookDescription);
    }
}
