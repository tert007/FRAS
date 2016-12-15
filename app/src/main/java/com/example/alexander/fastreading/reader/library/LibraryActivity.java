package com.example.alexander.fastreading.reader.library;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.library.fragment.library.ReaderLibraryFloatButtonOnClickResponse;
import com.example.alexander.fastreading.reader.library.fragment.library.ReaderLibraryFragment;
import com.example.alexander.fastreading.reader.library.fragment.description.ReaderBookDescriptionFragment;
import com.example.alexander.fastreading.reader.library.fragment.fileexplorer.ReaderFileExplorerFileExplorerFragment;
import com.example.alexander.fastreading.reader.reader.ReaderActivity;


public class LibraryActivity extends AppCompatActivity implements ReaderLibraryFloatButtonOnClickResponse, ReaderBookDescriptionResponse {

    private ReaderFileExplorerFileExplorerFragment fileExplorerFragment;
    private ReaderBookDescriptionFragment bookDescriptionFragment;
    private ReaderLibraryFragment readerLibraryFragment;

    private enum FragmentState { LIBRARY, FILE_EXPLORER, BOOK_DESCRIPTION }
    private FragmentState currentFragmentFragmentState;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_library_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
        }

        fragmentManager = getSupportFragmentManager();

        readerLibraryFragment = new ReaderLibraryFragment();
        readerLibraryFragment.addBookDelegate = this;
        readerLibraryFragment.bookClickDelegate = this;

        currentFragmentFragmentState = FragmentState.LIBRARY;

        fragmentManager.
                beginTransaction().
                replace(R.id.reader_library_fragment_container, readerLibraryFragment).
                commit();
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentFragmentState == FragmentState.LIBRARY) {
            super.onBackPressed();
        }

        if (currentFragmentFragmentState == FragmentState.FILE_EXPLORER) {
            if (fileExplorerFragment.onBackPressed()) {
                readerLibraryFragment = new ReaderLibraryFragment();
                readerLibraryFragment.addBookDelegate = this;
                readerLibraryFragment.bookClickDelegate = this;

                currentFragmentFragmentState = FragmentState.LIBRARY;

                fragmentManager.
                        beginTransaction().
                        replace(R.id.reader_library_fragment_container, readerLibraryFragment).
                        commit();
            }
        }

        if (currentFragmentFragmentState == FragmentState.BOOK_DESCRIPTION) {
            readerLibraryFragment = new ReaderLibraryFragment();
            readerLibraryFragment.addBookDelegate = this;
            readerLibraryFragment.bookClickDelegate = this;

            currentFragmentFragmentState = FragmentState.LIBRARY;

            fragmentManager.
                    beginTransaction().
                    replace(R.id.reader_library_fragment_container, readerLibraryFragment).
                    commit();
        }
    }

    @Override
    public void onFloatButtonClick() {
        fileExplorerFragment = new ReaderFileExplorerFileExplorerFragment();
        fileExplorerFragment.delegate = this;

        fragmentManager.
                beginTransaction().
                replace(R.id.reader_library_fragment_container, fileExplorerFragment).
                commit();

        currentFragmentFragmentState = FragmentState.FILE_EXPLORER;
    }

    //Откртытие уже добавленной книги или открытие уже существующей
    @Override
    public void bookResponse(BookDescription bookDescription) {
        bookDescriptionFragment = new ReaderBookDescriptionFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("book_description", bookDescription);

        bookDescriptionFragment.setArguments(bundle);

        fragmentManager.
                beginTransaction().
                replace(R.id.reader_library_fragment_container, bookDescriptionFragment).
                commit();

        currentFragmentFragmentState = FragmentState.BOOK_DESCRIPTION;
    }
}
