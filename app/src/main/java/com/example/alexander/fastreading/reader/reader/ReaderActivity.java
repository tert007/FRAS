package com.example.alexander.fastreading.reader.reader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.reader.dao.BookController;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.reader.settings.ReaderSettingsActivity;

/**
 * Created by Alexander on 23.09.2016.
 */
public class ReaderActivity extends AppCompatActivity implements FileReaderAsyncTaskResponse {

    private BookController bookController;

    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private String textSize;

    private TextView textView;
    private TextView speedTextView;
    private TextView speedResultTextView;
    private TextView currentPageTextView;
    private TextView currentPageResultTextView;
    private AppCompatSeekBar seekBar;
    private TextView currentPageSeekTextView;
    private TextView currentChapterTitleTextView;
    private View navigationLayout;

    private BookDescription bookDescription;
    private BookContent bookContent;

    private int currentPageIndex;
    private SeparatedBook separatedBook;

    private boolean itsSeekPause = true; // Seek Bar start status

    private boolean itsFastReading;
    private boolean fastReadingStarted;
    private WordSelector wordSelector;

    private int pageWidth;
    private int thirdPartOfPageWidth;

    private static final int defaultPageChangeDelay = 200;
    //private static final int wordLength = 6;

    private int currentSpeedIndex;
    private static final int[] speed = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000, 1050, 1100, 1150, 1200, 1250, 1300, 1350, 1400, 1450, 1500};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.file_opening_message));
        progressDialog.setCancelable(false);
        progressDialog.show();

        setContentView(R.layout.reader_activity);

        bookController = new BookController(this);

        Intent intent = getIntent();
        bookDescription = intent.getParcelableExtra("book_description"); //Change to id or filePath
        bookDescription = bookController.findBookDescription(bookDescription.getFilePath());

        FileReadingAsyncTask fileReadingAsyncTask;

        fileReadingAsyncTask = new FileReadingAsyncTask(this);
        fileReadingAsyncTask.delegate = this;
        fileReadingAsyncTask.execute(bookDescription);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle(bookDescription.getTitle());
            setSupportActionBar(toolbar);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        textView = (TextView) findViewById(R.id.reader_pages_text_view);
        textSize = preferences.getString("reader_text_size", getString(R.string.text_size_default_value));
        textView.setTextSize(Integer.valueOf(textSize));

        speedTextView = (TextView) findViewById(R.id.reader_pages_speed_text_view);
        speedResultTextView = (TextView) findViewById(R.id.reader_pages_speed_result_text_view);

        currentPageTextView = (TextView) findViewById(R.id.reader_pages_current_page_text_view);
        currentPageResultTextView = (TextView) findViewById(R.id.reader_pages_current_page_result_text_view);

        navigationLayout = findViewById(R.id.reader_pages_navigation_layout);
        currentChapterTitleTextView = (TextView) findViewById(R.id.reader_pages_current_chapter_title_text_view);
        currentPageSeekTextView = (TextView) findViewById(R.id.reader_pages_seek_text_view);
        seekBar = (AppCompatSeekBar) findViewById(R.id.reader_pages_seek_bar);

        currentChapterTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivity.this);
                builder.setTitle(R.string.select_chapter);

                final String[] items = separatedBook.getTitles().toArray(new String[separatedBook.getTitles().size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentPageIndex = separatedBook.getPageIndexByTitle(i);
                        currentPageResultTextView.setText(getCurrentPageByString());
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        seekBar.setProgress(currentPageIndex);
                        currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
                        textView.setText(separatedBook.getPage(currentPageIndex));

                        if (itsFastReading) {
                            wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        currentPageSeekTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NumberPicker numberPicker = new NumberPicker(ReaderActivity.this);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(separatedBook.size());
                numberPicker.setValue(currentPageIndex + 1);

                AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivity.this);
                builder.setTitle(R.string.select_page);
                builder.setCancelable(false);
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            dialogInterface.dismiss();

                            return true;
                        }

                        return false;
                    }
                });

                builder.setPositiveButton(R.string.select_page, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        numberPicker.clearFocus();

                        currentPageIndex = numberPicker.getValue() - 1;
                        currentPageResultTextView.setText(getCurrentPageByString());
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        seekBar.setProgress(currentPageIndex);
                        currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
                        textView.setText(separatedBook.getPage(currentPageIndex));

                        if (itsFastReading) {
                            wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
                        }

                        dialogInterface.dismiss();
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                final FrameLayout frameLayout = new FrameLayout(ReaderActivity.this);
                frameLayout.addView(numberPicker,
                        new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));

                builder.setView(frameLayout);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onFileReadingPostExecute(BookContent bookContent) {
        if (bookContent == null){
            progressDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(R.string.file_reading_error);

            builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    ReaderActivity.super.onBackPressed();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            return;
        }

        this.bookContent = bookContent;

        pageWidth = textView.getWidth();
        thirdPartOfPageWidth = pageWidth / 3;

        PageSplitter pageSplitter = new PageSplitter(textView.getPaint(), textView.getWidth(), textView.getHeight());
        separatedBook = pageSplitter.getSeparatedBook(bookContent);

        setCurrentPage();

        currentSpeedIndex = preferences.getInt("current_speed_index", 0);
        speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));

        textView.setText(separatedBook.getPage((currentPageIndex)));

        currentPageTextView.setVisibility(View.VISIBLE);
        currentPageResultTextView.setVisibility(View.VISIBLE);
        currentPageResultTextView.setText(getCurrentPageByString());

        speedTextView.setVisibility(View.GONE);
        speedResultTextView.setVisibility(View.GONE);

        textView.setOnTouchListener(onTouchListener);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        progressDialog.dismiss();

        showFastReadingModeDialog();
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
                fastReadingStarted = false;
                startActivity(new Intent(this, ReaderSettingsActivity.class));
                return true;
            case R.id.format_list:
                    showFastReadingModeDialog();
                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        fastReadingStarted = !fastReadingStarted;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.exit_message);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ReaderActivity.super.onBackPressed();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int getBookOffset() {
        int index = 0;
        for (int i = 0; i < currentPageIndex; i++){
            index += separatedBook.getPage(i).length();
        }

        if (currentPageIndex < separatedBook.size() - 1){
            return index + 1; //??
        }

        return index;
    }

    private int getProgress() {
        if (currentPageIndex == 0)
            return 0;

        return (int) (((float)(currentPageIndex + 1) * 100) / (float)separatedBook.size()) ;
    }

    private void setCurrentPage() {
        currentPageIndex = 0;

        long bookLength = 0;

        if (bookDescription.getBookOffset() <= separatedBook.getPage(0).length()){
            return;
        }

        for (int i = 0; i < separatedBook.size(); i++) {
            bookLength += separatedBook.getPage(i).length();

            if (bookLength <= bookDescription.getBookOffset()){
                currentPageIndex++;
            } else {
                break;
            }
        }
    }

    private String getCurrentPageByString(){
        return String.valueOf(currentPageIndex + 1) + '/' + separatedBook.size();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        final String currentTextSize = preferences.getString("reader_text_size", getString(R.string.text_size_default_value));

        if (!textSize.equals(currentTextSize)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.file_opening_message));
            progressDialog.setCancelable(false);
            progressDialog.show();

            textSize = currentTextSize;
            textView.setTextSize(Integer.valueOf(textSize));

            PageSplitter pageSplitter = new PageSplitter(textView.getPaint(), textView.getWidth(), textView.getHeight());
            separatedBook = pageSplitter.getSeparatedBook(bookContent);

            setCurrentPage();

            seekBar.setProgress(currentPageIndex);
            currentPageSeekTextView.setText(getCurrentPageByString());
            currentPageResultTextView.setText(getCurrentPageByString());
            currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
            textView.setText(separatedBook.getPage((currentPageIndex)));

            if (itsFastReading) {
                wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
            }

            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        fastReadingStarted = false;

        preferences.edit().
                putInt("current_speed_index", currentSpeedIndex).
                apply();

        bookDescription.setBookOffset(getBookOffset());
        bookDescription.setProgress(getProgress());
        bookController.updateBookDescription(bookDescription);
    }

    private void showFastReadingModeDialog() {
        final boolean itWasFastReading = itsFastReading;

        fastReadingStarted = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.reading_type));

        int index = 0;
        if (itsFastReading) {
            index = 1;
        }

        builder.setSingleChoiceItems(R.array.reading_type, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itsFastReading = i != 0;

                if (itsFastReading) {
                    speedTextView.setVisibility(View.VISIBLE);
                    speedResultTextView.setVisibility(View.VISIBLE);
                    speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));

                    textView.setOnTouchListener(fastReadingOnTouchListener);
                    seekBar.setOnSeekBarChangeListener(fastReadingSeekBarChangeListener);

                    if (!itWasFastReading) {
                        wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
                    }
                } else {
                    speedTextView.setVisibility(View.GONE);
                    speedResultTextView.setVisibility(View.GONE);

                    textView.setText(separatedBook.getPage(currentPageIndex));

                    textView.setOnTouchListener(onTouchListener);
                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Runnable wordSelectorThread = new Runnable() {
        @Override
        public void run() {
            if (fastReadingStarted){
                WorldSelectorPage worldSelectorPage;

                if ((worldSelectorPage = wordSelector.getNextSelectedWord()) != null){
                    textView.setText(worldSelectorPage.getPage());

                    double delay = 60_000 / speed[currentSpeedIndex];
                    //Log.d("LENGTH", String.valueOf(worldSelectorPage.getSelectedWordLength()));
                    //double delay = worldSelectorPage.getSelectedWordLength() * 1000 / (speed[currentSpeedIndex] * wordLength / 60);

                    textView.postDelayed(this, (int) delay);
                } else {
                    if (currentPageIndex <  separatedBook.size() - 1){
                        currentPageIndex++;
                        wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));

                        textView.postDelayed(this, defaultPageChangeDelay);

                        currentPageResultTextView.setText(getCurrentPageByString());
                    } else {
                        fastReadingStarted = false;
                        wordSelector = null;
                    }
                }
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();

                if (x < thirdPartOfPageWidth) {
                    itsSeekPause = true;
                    navigationLayout.setVisibility(View.GONE);

                    if (currentPageIndex > 0){
                        currentPageIndex--;
                        textView.setText(separatedBook.getPage(currentPageIndex));
                        currentPageResultTextView.setText(getCurrentPageByString());
                    }
                } else if (thirdPartOfPageWidth <= x && x <= pageWidth - thirdPartOfPageWidth){

                    itsSeekPause = !itsSeekPause;

                    if (!itsSeekPause) {
                        currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
                        seekBar.setMax(separatedBook.size() - 1);
                        seekBar.setProgress(currentPageIndex);
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        navigationLayout.setVisibility(View.VISIBLE);

                    } else {
                        navigationLayout.setVisibility(View.GONE);

                    }
                } else {
                    itsSeekPause = true;
                    navigationLayout.setVisibility(View.GONE);

                    if (currentPageIndex < separatedBook.size() - 1){
                        currentPageIndex++;
                        textView.setText(separatedBook.getPage(currentPageIndex));
                        currentPageResultTextView.setText(getCurrentPageByString());
                    }
                }
            }

            return false;
        }
    };

    private View.OnTouchListener fastReadingOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();

                if (x < thirdPartOfPageWidth) {
                    if (currentSpeedIndex > 0){
                        currentSpeedIndex--;
                        speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));
                    }
                } else if (thirdPartOfPageWidth <= x && x <= pageWidth - thirdPartOfPageWidth){
                    //itsSeekPause = !itsSeekPause;

                    fastReadingStarted = !fastReadingStarted;

                    if (currentPageIndex ==  separatedBook.size() - 1)
                        if (wordSelector == null)
                            fastReadingStarted = false;


                    if (fastReadingStarted) {
                        navigationLayout.setVisibility(View.GONE);
                        textView.post(wordSelectorThread);
                    } else {
                        currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
                        seekBar.setMax(separatedBook.size() - 1);
                        seekBar.setProgress(currentPageIndex);
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        navigationLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (currentSpeedIndex < speed.length - 1){
                        currentSpeedIndex++;
                        speedResultTextView.setText(String.valueOf(speed[currentSpeedIndex]));
                    }
                }
            }

            return false;
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                currentPageIndex = progress;
                currentPageSeekTextView.setText(getCurrentPageByString());
                currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            textView.setText(separatedBook.getPage(currentPageIndex));
            currentPageResultTextView.setText(getCurrentPageByString());
        }
    };

    private SeekBar.OnSeekBarChangeListener fastReadingSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                currentPageIndex = progress;
                currentPageSeekTextView.setText(getCurrentPageByString());
                currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            textView.setText(separatedBook.getPage(currentPageIndex));
            wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
            currentPageResultTextView.setText(getCurrentPageByString());
        }
    };
}
