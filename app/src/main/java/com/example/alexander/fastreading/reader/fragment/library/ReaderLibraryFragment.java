package com.example.alexander.fastreading.reader.fragment.library;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.BookDescription;
import com.example.alexander.fastreading.reader.dao.bookdescription.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescription.BookDescriptionDaoFactory;

import java.util.List;

/**
 * Created by Alexander on 03.09.2016.
 */
public class ReaderLibraryFragment extends Fragment implements ReaderLibraryOnBookClickResponse {

    public ReaderLibraryFloatButtonOnClickResponse addBookDelegate;
    public ReaderLibraryOnBookClickResponse bookClickDelegate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.reader_library_fragment, container, false);

        try {
            BookDescriptionDao bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(getActivity()).getBookDescriptionDao();
            List<BookDescription> bookDescriptions = bookDescriptionDao.getBookDescriptions();

            ReaderLibraryListViewAdapter listAdapter = new ReaderLibraryListViewAdapter(getActivity(), R.layout.reader_library_list_view_item, bookDescriptions);
            listAdapter.delegate = this;

            if (bookDescriptions.isEmpty()){
                TextView emptyLibraryTextView = (TextView) view.findViewById(R.id.reader_library_empty_library_text_view);
                emptyLibraryTextView.setVisibility(View.VISIBLE);
            } else {
                ListView listView = (ListView) view.findViewById(R.id.reader_library_list_view);
                listView.setAdapter(listAdapter);
            }

            FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.reader_library_floating_button);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBookDelegate.onFloatButtonClick();
                }
            });

        } catch (Exception e){
            ////
        }



        return view;
    }

    @Override
    public void onBookClick(BookDescription bookDescription) {
        bookClickDelegate.onBookClick(bookDescription);
    }
}
