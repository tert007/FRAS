package com.example.alexander.fastreading.reader.reader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.reader.fragment.reader.ReaderFragment;
import com.example.alexander.fastreading.reader.reader.fragment.reader.ReaderFragmentOnPauseResponse;
import com.example.alexander.fastreading.reader.reader.fragment.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 23.09.2016.
 */
public class ReaderActivity extends AppCompatActivity implements FileReaderAsyncTaskResponse,
        ReaderFragmentOnPauseResponse {

    private FragmentManager fragmentManager;
    private ProgressDialog progressDialog;

    private List<CharSequence> bookChapters;
    private BookDescription bookDescription;

    private BookDescriptionDao bookDescriptionDao;

    private enum ReaderState {READER, SETTINGS};
    private ReaderState currentState = ReaderState.READER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.file_opening_message));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent intent = getIntent();

        bookDescription = intent.getParcelableExtra("book_description");

        bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(this).getBookDescriptionDao();
        bookDescription = bookDescriptionDao.findBookDescription(bookDescription.getId());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(bookDescription.getTitle());
            setSupportActionBar(toolbar);
        }

        fragmentManager = getSupportFragmentManager();

        FileReadingAsyncTask fileReadingAsyncTask;

        fileReadingAsyncTask = new FileReadingAsyncTask(this);
        fileReadingAsyncTask.delegate = this;
        fileReadingAsyncTask.execute(bookDescription);
    }


    @Override
    public void onFileReadingPostExecute(List<CharSequence> response) {
        if (response == null){
            progressDialog.dismiss();
            Toast.makeText(this, "File reading error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        bookChapters = response;

        Bundle bundle = new Bundle();
        bundle.putParcelable("book_description", bookDescription);
        bundle.putCharSequenceArrayList("book", (ArrayList<CharSequence>) bookChapters);

        ReaderFragment readerFragment = new ReaderFragment();
        readerFragment.setArguments(bundle);
        readerFragment.onPauseDelegate = this;

        currentState = ReaderState.READER;

        fragmentManager.
                beginTransaction().
                replace(R.id.reader_fragment_container, readerFragment).
                commit();

        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                if (currentState == ReaderState.READER) {
                    currentState = ReaderState.SETTINGS;
                    fragmentManager.
                            beginTransaction().
                            replace(R.id.reader_fragment_container, new SettingsFragment()).
                            commit();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentState == ReaderState.SETTINGS) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("book_description", bookDescription);
            bundle.putCharSequenceArrayList("book", (ArrayList<CharSequence>) bookChapters);

            ReaderFragment readerFragment = new ReaderFragment();
            readerFragment.setArguments(bundle);
            readerFragment.onPauseDelegate = this;

            currentState = ReaderState.READER;

            fragmentManager.
                    beginTransaction().
                    replace(R.id.reader_fragment_container, readerFragment).
                    commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void readerFragmentOnPauseResponse(BookDescription bookDescription) {
        this.bookDescription = bookDescription;
    }

    @Override
    protected void onStop() {
        super.onStop();

        bookDescriptionDao.updateBookDescription(bookDescription);
    }
}
