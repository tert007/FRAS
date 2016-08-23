package com.example.alexander.fastreading.reader.fragment.pages;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander.fastreading.R;

import java.util.List;

/**
 * Created by Alexander on 19.08.2016.
 */
public class ReaderPagesFragment extends Fragment implements FileReaderAsyncTaskResponse {

    private TextView textView;
    private FileReadingAsyncTask fileReadingAsyncTask;

    private List<CharSequence> pages;
    private int currentPage;

    private WordSelector wordParser;

    private boolean itsStarted;
    private int delay = 50;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reader_pages_fragment, container, false);

        String filePath = getArguments().getString("file_path");

        fileReadingAsyncTask = new FileReadingAsyncTask(getActivity());
        fileReadingAsyncTask.delegate = this;
        fileReadingAsyncTask.execute(filePath);

        textView = (TextView) view.findViewById(R.id.reader_PAGES);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itsStarted = !itsStarted;

                if (itsStarted){
                    textView.postDelayed(wordSelector, delay);
                } else {
                    textView.removeCallbacks(wordSelector);
                }
            }
        });

        return view;
    }

    @Override
    public void onFileReadingPostExecute(String response) {
        PageSplitter pageSplitter = new PageSplitter(textView.getWidth(), textView.getHeight(), 1, 0);
        pageSplitter.append(response, textView.getPaint());
        pages = pageSplitter.getPages();

        textView.setText(pages.get(currentPage));

        wordParser = new WordSelector(pages.get(currentPage).toString());


    }


    private Runnable wordSelector = new Runnable() {
        @Override
        public void run() {
            if (itsStarted){
                String str;

                if ((str = wordParser.getNextSelectedWord()) != null){
                    textView.setText(Html.fromHtml(str));
                    textView.postDelayed(this, delay);
                } else {
                    if (currentPage < pages.size() - 1){

                        currentPage++;

                        wordParser = new WordSelector(pages.get(currentPage).toString());

                        textView.setText(pages.get(currentPage));
                        textView.postDelayed(this, delay);
                    } else {
                        itsStarted = false;
                    }
                }
            }

        }
    };

}
