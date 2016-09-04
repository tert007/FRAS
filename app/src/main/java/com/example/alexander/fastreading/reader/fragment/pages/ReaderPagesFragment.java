package com.example.alexander.fastreading.reader.fragment.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.SettingsManager;

import java.util.List;

/**
 * Created by Alexander on 19.08.2016.
 */
public class ReaderPagesFragment extends Fragment implements PagesFileReaderAsyncTaskResponse {

    private TextView textView;
    private PagesFileReadingAsyncTask pagesFileReadingAsyncTask;

    private List<CharSequence> pages;
    private int currentPage;

    private MyWordSelector wordParser;

    private boolean itsStarted;
    private int delay = 500;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_pages_fragment, container, false);

        String filePath = getArguments().getString("file_path");

        textView = (TextView) view.findViewById(R.id.reader_PAGES);
        textView.setTextSize(SettingsManager.getReaderTextSize());

        pagesFileReadingAsyncTask = new PagesFileReadingAsyncTask(getActivity());
        pagesFileReadingAsyncTask.delegate = this;
        pagesFileReadingAsyncTask.execute(filePath);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < pages.size() - 1){
                    currentPage++;
                    textView.setText(pages.get(currentPage));
                }

                /*
                itsStarted = !itsStarted;

                if (itsStarted){
                    textView.postDelayed(wordSelector, delay);
                } else {
                    textView.removeCallbacks(wordSelector);
                }
                */
            }
        });

        return view;
    }

    @Override
    public void onFileReadingPostExecute(List<CharSequence> response) {

        PageSplitter textSplitter = new PageSplitter(textView.getPaint(), textView.getWidth(), textView.getHeight());
        pages = textSplitter.getPages(response);
        textView.setText(pages.get(currentPage));

        //wordParser = new MyWordSelector(pages.get(currentPage));
        //Toast.makeText(getActivity(), a.getNextSelectedWord(), Toast.LENGTH_SHORT).show();
        //wordParser = new WordSelector(pages.get(currentPage));
    }

    /*
    private Runnable wordSelector = new Runnable() {
        @Override
        public void run() {
            if (itsStarted){
                Spanned str;

                if ((str = wordParser.getNextSelectedWord()) != null){
                    textView.setText(str);
                    textView.postDelayed(this, delay);
                } else {
                    if (currentPage <  pages.size() - 1){
                        currentPage++;
                        wordParser = new MyWordSelector(pages.get(currentPage));
                        textView.postDelayed(this, delay);
                    } else {
                        itsStarted = false;
                    }
                }
            }
        }
    };
    */

}
