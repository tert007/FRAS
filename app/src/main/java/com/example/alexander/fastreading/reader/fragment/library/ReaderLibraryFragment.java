package com.example.alexander.fastreading.reader.fragment.library;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.dao.bookdao.BookDao;
import com.example.alexander.fastreading.reader.dao.bookdao.BookDaoFactory;
import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by Alexander on 03.09.2016.
 */
public class ReaderLibraryFragment extends Fragment implements ReaderLibraryOnBookClickResponse, ReaderLibraryRemoveBookOnClickResponse {

    public ReaderLibraryFloatButtonOnClickResponse addBookDelegate;
    public ReaderLibraryOnBookClickResponse bookClickDelegate;

    private ListView listView;
    private ReaderLibraryListViewAdapter listAdapter;

    private List<BookDescription> bookDescriptions;

    private TextView emptyLibraryTextView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.reader_library_fragment, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.library));

        try {
            BookDescriptionDao bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(getActivity()).getBookDescriptionDao();
            bookDescriptions = bookDescriptionDao.getBookDescriptions();

            emptyLibraryTextView = (TextView) view.findViewById(R.id.reader_library_empty_library_text_view);
            listView = (ListView) view.findViewById(R.id.reader_library_list_view);

            listAdapter = new ReaderLibraryListViewAdapter(getActivity(), R.layout.reader_library_list_view_item, bookDescriptions);
            listAdapter.bookClickDelegate = this;
            listAdapter.removeBookDelegate = this;

            if (bookDescriptions.isEmpty()){
                emptyLibraryTextView.setVisibility(View.VISIBLE);
                listView.setEmptyView(emptyLibraryTextView);
            } else {
                emptyLibraryTextView.setVisibility(View.GONE);
                listView.setAdapter(listAdapter);
            }

            FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.reader_library_floating_button);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBookDelegate.onFloatButtonClick();
                }
            });

        } catch (BookParserException e){
            Snackbar.make(view, "Error", Snackbar.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onBookClick(BookDescription bookDescription) {
        bookClickDelegate.onBookClick(bookDescription);
    }

    @Override
    public void onBookRemoveClick(final BookDescription bookDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(bookDescription.getTitle());
        builder.setMessage(getString(R.string.book_remove_message));

        String positiveText = getString(R.string.remove);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BookDao bookDao = new BookDaoFactory(getActivity()).getBookDao(bookDescription.getType());
                        bookDao.removeBook(bookDescription);

                        listAdapter.remove(bookDescription);
                        listAdapter.notifyDataSetChanged();

                        if (listAdapter.isEmpty()){
                            emptyLibraryTextView.setVisibility(View.VISIBLE);
                            listView.setEmptyView(emptyLibraryTextView);
                        } else {
                            emptyLibraryTextView.setVisibility(View.GONE);
                        }
                    }
                });

        String negativeText = getString(R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
