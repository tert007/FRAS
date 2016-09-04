package com.example.alexander.fastreading.reader.fragment.library;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.bookparser.BookDescription;
import com.example.alexander.fastreading.reader.dao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.DaoFactory;

import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 03.09.2016.
 */
public class ReaderLibraryFragment extends Fragment {

    private ReaderLibraryListViewAdapter listAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.reader_library_fragment, container, false);

        BookDescriptionDao bookDescriptionDao = DaoFactory.getDaoFactory(getActivity()).getBookDescriptionDao();
        List<BookDescription> a = bookDescriptionDao.getBookDescriptions();

        listAdapter = new ReaderLibraryListViewAdapter(getActivity(), R.layout.reader_library_list_view_item, a);

        ListView listView = (ListView) view.findViewById(R.id.reader_library_list_view);
        listView.setAdapter(listAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(view, "Hello Snackbar", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
