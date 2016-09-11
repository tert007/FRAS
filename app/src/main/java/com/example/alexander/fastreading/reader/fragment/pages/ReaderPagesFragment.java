package com.example.alexander.fastreading.reader.fragment.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private int delay = 150;

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

    @Override
    public void onFileReadingPostExecute(List<CharSequence> response) {
        PageSplitter textSplitter = new PageSplitter(textView.getPaint(), textView.getWidth(), textView.getHeight());

        pages = textSplitter.getPages(response);

        if (bookDescription.getProgress() == 0f) {
            currentPage = 0;
        } else if (bookDescription.getProgress() == 100f){
            currentPage = pages.size() - 1;
        } else {
            currentPage = (Math.round(pages.size() * (bookDescription.getProgress() / 100f ))) - 1;
        }

        textView.setText(pages.get(currentPage));

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
                CharSequence str;

                if ((str = wordSelector.getNextSelectedWord()) != null){
                    textView.setText(str);
                    textView.postDelayed(this, delay);
                } else {
                    if (currentPage <  pages.size() - 1){
                        currentPage++;
                        wordSelector = new WordSelector(pages.get(currentPage));
                        textView.postDelayed(this, delay);

                        String currentPageText = String.valueOf(currentPage + 1) + '/' + pages.size();
                        currentPageResultTextView.setText(currentPageText);
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
                itsStarted = !itsStarted;

                if (itsStarted) {
                    textView.post(wordSelectorThread);
                }
            }

            return false;
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        float progress;
        if (currentPage == 0){
            progress = 0f;
        } else if (currentPage == pages.size() - 1){
            progress = 100f;
        } else {
            progress = (currentPage + 1) * 100f / (float) pages.size();
        }
        bookDescription.setProgress(progress);
        BookDescriptionDaoFactory.getDaoFactory(getActivity()).getBookDescriptionDao().updateBookDescription(bookDescription);
    }
}
