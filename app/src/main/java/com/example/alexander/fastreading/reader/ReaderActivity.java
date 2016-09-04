package com.example.alexander.fastreading.reader;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.bookparser.BookDescription;
import com.example.alexander.fastreading.reader.bookparser.trash.Book;
import com.example.alexander.fastreading.reader.dao.DaoFactory;
import com.example.alexander.fastreading.reader.fragment.library.ReaderLibraryFragment;
import com.example.alexander.fastreading.reader.fragment.setting.ReaderSettingFragment;
import com.example.alexander.fastreading.reader.fragment.description.ReaderBookDescriptionFragment;
import com.example.alexander.fastreading.reader.fragment.fileexplorer.ReaderFileExplorerFileExplorerFragment;
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
    private ReaderSettingFragment readerSettingFragment;
    private ReaderLibraryFragment readerLibraryFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private File currentDirectory;

    private boolean itsSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);

        readerLibraryFragment = new ReaderLibraryFragment();

        //fileExplorerFragment = new ReaderFileExplorerFileExplorerFragment();
        //fileExplorerFragment.delegate = this;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);

        fragmentManager = getFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reader_fragment_container, readerLibraryFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_toolbar, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (itsSettings){
            itsSettings = false;
            fileExplorerFragment = new ReaderFileExplorerFileExplorerFragment();
            fileExplorerFragment.delegate = this;

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.reader_fragment_container, fileExplorerFragment);
            fragmentTransaction.commit();
            return;
        }

        if (currentDirectory.getParentFile() != null){
            currentDirectory = currentDirectory.getParentFile();

            changeDirectory(currentDirectory);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void fileOnClick(File file) {
        //Уже нажал на книгу из списка
        if (file.isDirectory()){
            currentDirectory = file;

            changeDirectory(currentDirectory);
        } else {
            BookDescription a = new BookDescription();
            a.setFilePath(file.getPath());
            a.setAuthor("Test");
            a.setFavorite(true);
            a.setType("txt");
            a.setLanguage("en");
            a.setProgress(1);
            a.setTitle("Title");

            long b = DaoFactory.getDaoFactory(getApplication()).getBookDescriptionDao().add(a);

            Toast.makeText(getApplication(), String.valueOf(b), Toast.LENGTH_SHORT).show();

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
