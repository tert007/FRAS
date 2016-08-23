package com.example.alexander.fastreading.reader;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.book.Book;
import com.example.alexander.fastreading.reader.fragment.description.ReaderBookDescriptionFragment;
import com.example.alexander.fastreading.reader.fragment.ReaderFileExplorerFileExplorerFragment;
import com.example.alexander.fastreading.reader.fragment.description.ReaderPagesReadBookResponse;
import com.example.alexander.fastreading.reader.fragment.description.ReaderScrollReadBookResponse;
import com.example.alexander.fastreading.reader.fragment.pages.ReaderPagesFragment;
import com.example.alexander.fastreading.reader.fragment.scroll.ReaderScrollFragment;

import java.io.File;


public class ReaderActivity extends AppCompatActivity implements ReaderFileExplorerOnClickResponse,
        ReaderScrollReadBookResponse, ReaderPagesReadBookResponse {

    private ReaderFileExplorerFileExplorerFragment fileExplorerFragment;
    private ReaderScrollFragment scrollFragment;
    private ReaderPagesFragment pagesFragment;
    private ReaderBookDescriptionFragment bookDescriptionFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private File currentDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);



        fileExplorerFragment = new ReaderFileExplorerFileExplorerFragment();
        fileExplorerFragment.delegate = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);

        fragmentManager = getFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, fileExplorerFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        if (currentDirectory.getParentFile() != null){
            currentDirectory = currentDirectory.getParentFile();

            changeDirectory(currentDirectory);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings :

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
    public void fileOnClick(File file) {
        //Уже нажал на книгу из списка
        if (file.isDirectory()){
            currentDirectory = file;

            changeDirectory(currentDirectory);
        } else {
            bookDescriptionFragment = new ReaderBookDescriptionFragment();
            bookDescriptionFragment.scrollDelegate = this;
            bookDescriptionFragment.pagesDelegate = this;

            Bundle bundle = new Bundle();
            bundle.putString("file_path", file.getPath());

            bookDescriptionFragment.setArguments(bundle);

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.reader_fragment_container, bookDescriptionFragment);
            fragmentTransaction.commit();
        }
    }

    private void changeDirectory(File directory){
        fileExplorerFragment = new ReaderFileExplorerFileExplorerFragment();
        fileExplorerFragment.setStartDirectoryPath(directory);
        fileExplorerFragment.delegate = this;

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, fileExplorerFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onScrollReadBookClick(Book book) {
        scrollFragment = new ReaderScrollFragment();

        Bundle bundle = new Bundle();
        bundle.putString("file_path", book.getFilePath());

        scrollFragment.setArguments(bundle);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, scrollFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPagesReadBookClick(Book book) {
        pagesFragment = new ReaderPagesFragment();

        Bundle bundle = new Bundle();
        bundle.putString("file_path", book.getFilePath());

        pagesFragment.setArguments(bundle);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, pagesFragment);
        fragmentTransaction.commit();
    }
}
