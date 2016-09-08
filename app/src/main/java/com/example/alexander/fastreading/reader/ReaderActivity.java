package com.example.alexander.fastreading.reader;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.fragment.description.ReaderScrollReadBookResponse;
import com.example.alexander.fastreading.reader.fragment.fileexplorer.ReaderFileExplorerBookAddResponse;
import com.example.alexander.fastreading.reader.fragment.library.ReaderLibraryFloatButtonOnClickResponse;
import com.example.alexander.fastreading.reader.fragment.library.ReaderLibraryFragment;
import com.example.alexander.fastreading.reader.fragment.library.ReaderLibraryOnBookClickResponse;
import com.example.alexander.fastreading.reader.fragment.setting.ReaderSettingFragment;
import com.example.alexander.fastreading.reader.fragment.description.ReaderBookDescriptionFragment;
import com.example.alexander.fastreading.reader.fragment.fileexplorer.ReaderFileExplorerFileExplorerFragment;
import com.example.alexander.fastreading.reader.fragment.pages.ReaderPagesFragment;
import com.example.alexander.fastreading.reader.fragment.scroll.ReaderScrollFragment;


public class ReaderActivity extends AppCompatActivity implements ReaderScrollReadBookResponse, /*ReaderPagesReadBookResponse,*/ ReaderLibraryFloatButtonOnClickResponse,
        ReaderLibraryOnBookClickResponse, ReaderFileExplorerBookAddResponse {

    private ReaderFileExplorerFileExplorerFragment fileExplorerFragment;
    private ReaderScrollFragment scrollFragment;
    private ReaderPagesFragment pagesFragment;
    private ReaderBookDescriptionFragment bookDescriptionFragment;
    private ReaderSettingFragment readerSettingFragment;
    private ReaderLibraryFragment readerLibraryFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private boolean itsFileExplorerPreviousValue;
    private boolean itsFileExplorer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        fragmentManager = getFragmentManager();

        readerLibraryFragment = new ReaderLibraryFragment();
        readerLibraryFragment.addBookDelegate = this;
        readerLibraryFragment.bookClickDelegate = this;

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, readerLibraryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                itsFileExplorerPreviousValue = itsFileExplorer;
                itsFileExplorer = false;

                readerSettingFragment = new ReaderSettingFragment();

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.reader_fragment_container, readerSettingFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (itsFileExplorer) {
            if (fileExplorerFragment.onBackPressed()) {
                itsFileExplorer = false;
            } else {
                return;
            }
        }

        if (fragmentManager.getBackStackEntryCount() > 0 ) {
            itsFileExplorer = itsFileExplorerPreviousValue;
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFloatButtonClick() {
        itsFileExplorer = true;

        fileExplorerFragment = new ReaderFileExplorerFileExplorerFragment();
        fileExplorerFragment.delegate = this;

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, fileExplorerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBookClick(BookDescription bookDescription) {
        bookDescriptionFragment = new ReaderBookDescriptionFragment();
        bookDescriptionFragment.scrollDelegate = this;
        //bookDescriptionFragment.pagesDelegate = this;

        Bundle bundle = new Bundle();
        bundle.putParcelable("book_description", bookDescription);

        bookDescriptionFragment.setArguments(bundle);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, bookDescriptionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void bookAddPostExecute(BookDescription bookDescription) {
        bookDescriptionFragment = new ReaderBookDescriptionFragment();
        bookDescriptionFragment.scrollDelegate = this;
        //bookDescriptionFragment.pagesDelegate = this;

        Bundle bundle = new Bundle();
        bundle.putParcelable("book_description", bookDescription);

        bookDescriptionFragment.setArguments(bundle);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, bookDescriptionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onScrollReadBookClick(BookDescription bookDescription) {
        scrollFragment = new ReaderScrollFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("book_description", bookDescription);

        scrollFragment.setArguments(bundle);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, scrollFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
