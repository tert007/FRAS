package com.example.alexander.fastreading.reader.library.fragment.library;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.dao.BookController;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.library.ReaderBookDescriptionResponse;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 03.09.2016.
 */
public class ReaderLibraryFragment extends Fragment implements ReaderBookDescriptionResponse, ReaderLibraryRemoveBookOnClickResponse {

    private BookController bookController;

    public ReaderLibraryFloatButtonOnClickResponse addBookDelegate;
    public ReaderBookDescriptionResponse bookClickDelegate;

    private ListView listView;
    private ReaderLibraryListViewAdapter listAdapter;

    private List<BookDescription> bookDescriptions = Collections.emptyList();

    private TextView emptyLibraryTextView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.reader_library_fragment, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.library));

        bookController = new BookController(getActivity());
        bookDescriptions = bookController.getBookDescriptions();

        boolean bookLibraryWasChanged = false;

        for (int i = 0; i < bookDescriptions.size(); i++) {
            if (! (new File(bookDescriptions.get(i).getFilePath())).exists()) {
                bookController.removeBook(bookDescriptions.get(i));
                bookDescriptions.remove(i);

                bookLibraryWasChanged = true;
            }
        }

        if (bookLibraryWasChanged) {
            Snackbar.make(view, getString(R.string.library_was_changed), Snackbar.LENGTH_LONG).show();
        }

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

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                    } else {
                        Snackbar.make(getView(), R.string.permission_error, Snackbar.LENGTH_LONG).setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                                startActivity(appSettingsIntent);
                            }
                        }).show();
                    }
                } else {
                    addBookDelegate.onFloatButtonClick();
                }
            }
        });

        return view;
    }

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Snackbar.make(getView(), R.string.permission_error, Snackbar.LENGTH_LONG).show();
            } else {
                addBookDelegate.onFloatButtonClick();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //onBookClick
    @Override
    public void bookResponse(BookDescription bookDescription) {
        bookClickDelegate.bookResponse(bookDescription);
    }

    @Override
    public void onBookRemoveClick(final BookDescription bookDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.library_book_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setTitle(bookDescription.getTitle());
                    builder.setMessage(getString(R.string.book_remove_message));

                    builder.setPositiveButton(R.string.remove,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bookController.removeBook(bookDescription);

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

                    builder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
