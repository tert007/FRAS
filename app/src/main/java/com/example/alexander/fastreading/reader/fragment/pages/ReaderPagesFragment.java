package com.example.alexander.fastreading.reader.fragment.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 19.08.2016.
 */
public class ReaderPagesFragment extends Fragment implements PagesFileReaderAsyncTaskResponse {

    private TextView textView;

    private TextView speedTextView;
    private TextView speedResultTextView;

    private TextView currentPageTextView;
    private TextView currentPageResultTextView;

    private boolean itsFastReading;

    private BookDescription bookDescription;
    private int currentPage;
    private List<CharSequence> pages;

    private WordSelector wordSelector;

    //Will be recreated
    private boolean itsStarted;

    private int widthDevidedByTree;
    private int width;

    private static final int defaultPageChangeDelay = 200;

    private static final int wordLength = 6;
    private static final int[] speed = {100, 150, 200, 250, 300, 350, 400, 450, 500, 600, 650, 700, 750, 800, 850, 900, 950, 1000};
    private int currentSpeedIndex;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_pages_fragment, container, false);

        textView = (TextView) view.findViewById(R.id.reader_pages_text_view);
        textView.setTextSize(SettingsManager.getReaderTextSize());

        speedTextView = (TextView) view.findViewById(R.id.reader_pages_speed_text_view);
        speedResultTextView = (TextView) view.findViewById(R.id.reader_pages_speed_result_text_view);

        currentPageTextView = (TextView) view.findViewById(R.id.reader_pages_current_page_text_view);
        currentPageResultTextView = (TextView) view.findViewById(R.id.reader_pages_current_page_result_text_view);

        bookDescription = (BookDescription) getArguments().getParcelable("book_description");
        itsFastReading = getArguments().getInt("fast_reading") == 1;

        if (itsFastReading) {
            speedTextView.setVisibility(View.VISIBLE);
            speedResultTextView.setVisibility(View.VISIBLE);
            speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));
            textView.setOnTouchListener(fastReadingOnTouchListener);
        } else {
            speedTextView.setVisibility(View.GONE);
            speedResultTextView.setVisibility(View.GONE);
            textView.setOnTouchListener(onTouchListener);
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(bookDescription.getTitle());

        PagesFileReadingAsyncTask pagesFileReadingAsyncTask;

        pagesFileReadingAsyncTask = new PagesFileReadingAsyncTask(getActivity());
        pagesFileReadingAsyncTask.delegate = this;
        pagesFileReadingAsyncTask.execute(bookDescription);

        return view;
    }

    private int getBookOffset() {
        /*
        int index = 0;
        for (int i = 0; i < currentPage; i++){
            index += pages.get(i).length();
        }

        return index;
        */
        return 0;
    }

    private void setCurrentPage() {
        /*
        if (pages.isEmpty())
            return;

        long bookLength = 0;

        if (bookDescription.getBookOffset() < pages.get(0).length()){
            return;
        }

        for (CharSequence page : pages) {
            bookLength += page.length();

            if (bookLength < bookDescription.getBookOffset()){
                currentPage++;
            } else {
                break;
            }
        }
        */
    }

    @Override
    public void onFileReadingPostExecute(List<CharSequence> response) {
        if (response == null){
            Toast.makeText(getActivity(), "File reading error", Toast.LENGTH_SHORT).show();
            return;
        }

        PageSplitter pageSplitter = new PageSplitter(textView.getPaint(), textView.getWidth(), textView.getHeight());
        pages = pageSplitter.getPages(response);

        setCurrentPage();

        textView.setText(pages.get(currentPage));

        width = textView.getWidth();
        widthDevidedByTree = width / 3;

        currentPageTextView.setVisibility(View.VISIBLE);
        currentPageResultTextView.setVisibility(View.VISIBLE);

        currentPageResultTextView.setText(getCurrentPage());

        if (itsFastReading){
            itsStarted = false;
            wordSelector = new WordSelector(pages.get(currentPage));
        }
    }

    private String getCurrentPage(){
        return String.valueOf(currentPage + 1) + '/' + pages.size();
    }

    private Runnable wordSelectorThread = new Runnable() {
        @Override
        public void run() {
            if (itsStarted){
                WorldSelectorPage worldSelectorPage;

                if ((worldSelectorPage = wordSelector.getNextSelectedWord()) != null){
                    textView.setText(worldSelectorPage.getPage());

                    //Log.d("LENGTH", String.valueOf(worldSelectorPage.getSelectedWordLength()));
                    double delay = worldSelectorPage.getSelectedWordLength() * 1000 / (speed[currentSpeedIndex] * wordLength / 60);
                    //Log.d("SPEED", String.valueOf(delay));

                    textView.postDelayed(this, (int) delay);
                } else {
                    if (currentPage <  pages.size() - 1){
                        currentPage++;
                        wordSelector = new WordSelector(pages.get(currentPage));

                        textView.postDelayed(this, defaultPageChangeDelay);

                        currentPageResultTextView.setText(getCurrentPage());
                    } else {
                        itsStarted = false;
                    }
                }
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (textView.getWidth() / 2 < event.getX()){
                    if (currentPage < pages.size() - 1){
                        currentPage++;
                        textView.setText(pages.get(currentPage));
                        currentPageResultTextView.setText(getCurrentPage());
                    }
                } else {
                    if (currentPage > 0){
                        currentPage--;
                        textView.setText(pages.get(currentPage));
                        currentPageResultTextView.setText(getCurrentPage());
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

                if (x < widthDevidedByTree) {
                    if (currentSpeedIndex > 0){
                        currentSpeedIndex--;
                        speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));
                    }
                } else if ( widthDevidedByTree <= x && x <= width - widthDevidedByTree){
                    itsStarted = !itsStarted;

                    if (itsStarted) {
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
        BookDescriptionDaoFactory.getDaoFactory(getActivity()).getBookDescriptionDao().updateBookDescription(bookDescription);
    }
}
