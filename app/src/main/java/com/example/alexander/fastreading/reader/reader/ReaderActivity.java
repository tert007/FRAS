package com.example.alexander.fastreading.reader.reader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.example.alexander.fastreading.R;
import com.example.alexander.fastreading.app.SettingsManager;
import com.example.alexander.fastreading.reader.dao.BookController;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.reader.settings.ReaderSettingsActivity;

/**
 * Created by Alexander on 23.09.2016.
 */
public class ReaderActivity extends AppCompatActivity implements FileReaderAsyncTaskResponse, BillingProcessor.IBillingHandler {

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

    //private boolean itsFastReading;
    private boolean fastReadingStarted;
    private WordSelector wordSelector;

    private enum ReadingMode {READING_MODE, FAST_READING_MODE, FLASH_READING_MODE};
    private ReadingMode readingMode;

    private int pageWidth;
    private int thirdPartOfPageWidth;

    private static final int defaultPageChangeDelay = 200;
    //private static final int wordLength = 6;

    private ProgressBar flashModeProgressBar;

    private int speedIndex;

    private static final int[] premiumSpeedArray = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000, 1050, 1100, 1150, 1200, 1250, 1300, 1350, 1400, 1450, 1500, 1550, 1600, 1650, 1700, 1750, 1800, 1850, 1900, 1950, 2000};
    private static final int[] speedArray = {100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750};

    private static int[] currentSpeedArray;

    BillingProcessor billingProcessor;

    private boolean isPremiumUser;

    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArncdZCT0YWSgOsMQxtFnJKkEwd4b8qE68HT1SHr+2GTwqluicDTBA1aHliTlwWBI9WROLAFK9xYeuxB2IYUWU73XBU9bMEGBzlEdOnCl9h4DT/4Qw0oxu5UILSTX6YShCSSohgWA1Q91/Y9k22UPFbgdjULJtGmGMhXZC9Tho72ctyKM0j1qjHkuu4OoznVy4aqxBK/bfytt5+nZI3lIft3U8FZ5nVsei4MoW7bZmGtb5vr0ZbEPfWHCi6MtH0HopSf7f9NWSWD3RyvfTpmdbh4DIo+JghHt5L08HmscZCNaGvBQzSx+uGqLZdgYYYQGhRSk5NsvrOMbqpo17lJmxwIDAQAB";
    private static final String PREMIUM_USER_SKU = "com.speedreading.alexander.speedreading.premium";

    View parentView;

    public void purchasePremium() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(R.layout.premim_dialog);

        builder.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                dialogInterface.dismiss();

                boolean isAvailable = BillingProcessor.isIabServiceAvailable(ReaderActivity.this);
                if (isAvailable) {
                    billingProcessor.purchase(ReaderActivity.this, PREMIUM_USER_SKU);
                } else {
                    Snackbar.make(parentView, R.string.billing_system_is_not_available, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        if (productId.equals(PREMIUM_USER_SKU)) {

            isPremiumUser = true;
            SettingsManager.setPremiumUser(true);
            currentSpeedArray = premiumSpeedArray;
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        if (billingProcessor.listOwnedProducts().contains(PREMIUM_USER_SKU)) {
            if ( ! isPremiumUser) {
                isPremiumUser = true;
                SettingsManager.setPremiumUser(true);
                currentSpeedArray = premiumSpeedArray;
            }
        } else {
            isPremiumUser = false;
            SettingsManager.setPremiumUser(false);
            currentSpeedArray = speedArray;
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Snackbar.make(parentView, R.string.billing_system_error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( ! billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.file_opening_message));
        progressDialog.setCancelable(false);
        progressDialog.show();

        setContentView(R.layout.reader_activity);

        isPremiumUser = SettingsManager.isPremiumUser();

        if (isPremiumUser) {
            currentSpeedArray = premiumSpeedArray;
        } else {
            currentSpeedArray = speedArray;
        }

        billingProcessor = new BillingProcessor(this, base64EncodedPublicKey, this);

        parentView = findViewById(android.R.id.content);

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

        readingMode = ReadingMode.READING_MODE;

        textView = (TextView) findViewById(R.id.reader_pages_text_view);
        textSize = preferences.getString("reader_text_size", getString(R.string.text_size_default_value));
        textView.setTextSize(Integer.valueOf(textSize));

        speedTextView = (TextView) findViewById(R.id.reader_pages_speed_text_view);
        speedResultTextView = (TextView) findViewById(R.id.reader_pages_speed_result_text_view);

        currentPageTextView = (TextView) findViewById(R.id.reader_pages_current_page_text_view);
        currentPageResultTextView = (TextView) findViewById(R.id.reader_pages_current_page_result_text_view);

        flashModeProgressBar = (ProgressBar) findViewById(R.id.reader_flash_mode_progress_bar);

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

                        if (readingMode == ReadingMode.FLASH_READING_MODE) {
                            flashModeCurrentTime = 0;
                            flashModeProgressBar.setProgress(0);
                        }

                        if (readingMode == ReadingMode.FAST_READING_MODE) {
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

                        if (readingMode == ReadingMode.FLASH_READING_MODE) {
                            flashModeCurrentTime = 0;
                            flashModeProgressBar.setProgress(0);
                        }

                        if (readingMode == ReadingMode.FAST_READING_MODE) {
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
                    finish();
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
        separatedBook = pageSplitter.getSeparatedBook(this.bookContent);

        setCurrentPage();

        speedIndex = preferences.getInt("current_speed_index", 0);
        if (currentSpeedArray.length < speedIndex) {
            speedIndex = currentSpeedArray.length - 1;
        }

        speedResultTextView.setText(String.valueOf(currentSpeedArray[speedIndex]));

        textView.setText(separatedBook.getPage((currentPageIndex)));

        currentPageTextView.setVisibility(View.VISIBLE);
        currentPageResultTextView.setVisibility(View.VISIBLE);
        currentPageResultTextView.setText(getCurrentPageByString());

        speedTextView.setVisibility(View.INVISIBLE);
        speedResultTextView.setVisibility(View.INVISIBLE);

        textView.setOnTouchListener(readingListenerOnTouchListener);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        progressDialog.dismiss();

        if (SettingsManager.isReaderShowHelp()) {
            showGuideDialog();
        } else {
            showFastReadingModeDialog();
        }
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

        if ( ! textSize.equals(currentTextSize)) {
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

            if (readingMode == ReadingMode.FAST_READING_MODE) {
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
                putInt("current_speed_index", speedIndex).
                apply();

        bookDescription.setBookOffset(getBookOffset());
        bookDescription.setProgress(getProgress());
        bookController.updateBookDescription(bookDescription);
    }

    private void showGuideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View guideView = getLayoutInflater().inflate(R.layout.reader_guide_dialog, null);
        final AppCompatCheckBox checkBox = (AppCompatCheckBox) guideView.findViewById(R.id.reader_guide_dialog_dont_show_again_check_box);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsManager.setReaderShowHelp( ! checkBox.isChecked());
            }
        });

        builder.setView(guideView);

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showFastReadingModeDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFastReadingModeDialog() {
        final boolean itWasFastReading = (readingMode == ReadingMode.FAST_READING_MODE);
        final boolean itWasFlashReading = (readingMode == ReadingMode.FLASH_READING_MODE);

        fastReadingStarted = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.reading_type));

        int index = 0;
        switch (readingMode) {
            case READING_MODE:
                index = 0;
                break;
            case FAST_READING_MODE:
                index = 1;
                break;
            case FLASH_READING_MODE:
                index = 2;
                break;
        }

        builder.setSingleChoiceItems(R.array.reading_type, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        readingMode = ReadingMode.READING_MODE;
                        break;
                    case 1:
                        readingMode = ReadingMode.FAST_READING_MODE;
                        break;
                    case 2:
                        readingMode = ReadingMode.FLASH_READING_MODE;
                        break;
                }

                if (readingMode == ReadingMode.FAST_READING_MODE) {
                    if ( ! itWasFastReading) {
                        flashModeProgressBar.setProgress(0);
                        flashModeCurrentTime = 0;

                        speedTextView.setVisibility(View.VISIBLE);
                        speedResultTextView.setVisibility(View.VISIBLE);
                        speedResultTextView.setText(String.valueOf(currentSpeedArray[speedIndex]));

                        flashModeProgressBar.setVisibility(View.INVISIBLE);

                        textView.setOnTouchListener(fastReadingOnTouchListener);
                        seekBar.setOnSeekBarChangeListener(fastReadingSeekBarChangeListener);

                        wordSelector = new WordSelector(separatedBook.getPage(currentPageIndex));
                    }
                } else if (readingMode == ReadingMode.FLASH_READING_MODE) {
                    if ( ! itWasFlashReading) {
                        flashModeProgressBar.setProgress(0);
                        flashModeCurrentTime = 0;

                        speedTextView.setVisibility(View.VISIBLE);
                        speedResultTextView.setVisibility(View.VISIBLE);
                        speedResultTextView.setText(String.valueOf(currentSpeedArray[speedIndex]));

                        textView.setText(separatedBook.getPage(currentPageIndex));

                        flashModeProgressBar.setVisibility(View.VISIBLE);

                        textView.setOnTouchListener(flashReadingOnTouchListener);
                        seekBar.setOnSeekBarChangeListener(flashReadingSeekBarChangeListener);
                    }
                } else if (readingMode == ReadingMode.READING_MODE) {
                    flashModeProgressBar.setProgress(0);
                    flashModeCurrentTime = 0;

                    speedTextView.setVisibility(View.INVISIBLE);
                    speedResultTextView.setVisibility(View.INVISIBLE);
                    flashModeProgressBar.setVisibility(View.INVISIBLE);

                    textView.setText(separatedBook.getPage(currentPageIndex));

                    textView.setOnTouchListener(readingListenerOnTouchListener);
                    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Runnable fastReadingThread = new Runnable() {
        @Override
        public void run() {
            if (fastReadingStarted){
                WorldSelectorPage worldSelectorPage;

                if ((worldSelectorPage = wordSelector.getNextSelectedWord()) != null){
                    textView.setText(worldSelectorPage.getPage());

                    double delay = 60_000 / currentSpeedArray[speedIndex];
                    //Log.d("LENGTH", String.valueOf(worldSelectorPage.getSelectedWordLength()));
                    //double delay = worldSelectorPage.getSelectedWordLength() * 1000 / (currentSpeedArray[speedIndex] * wordLength / 60);

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

    private boolean flashChanged;
    private int flashModeCurrentTime;

    private Runnable flashReadingThread = new Runnable() {

        private static final int FLASH_MODE_PROGRESS_DELAY = 100;

        private CharSequence currentPage;
        private int flashModeCurrentPageDelay;

        public int getFlashModeReadingDelay(CharSequence page, int speed) {
            int wordsCount = page.toString().split("[\\s']").length;
            return (60_000 / speed) * wordsCount;
        }

        @Override
        public void run() {
            if (fastReadingStarted) {
                if (flashChanged) {
                    if (currentPage == null) {
                        currentPage = separatedBook.getPage(currentPageIndex);
                    }

                    int oldDelay = flashModeCurrentPageDelay;
                    int remainder = oldDelay - flashModeCurrentTime;

                    float ratio = (float) remainder / (float) oldDelay;
                    int newRemainder = (int) (getFlashModeReadingDelay(currentPage, currentSpeedArray[speedIndex]) * ratio);

                    flashModeCurrentPageDelay = oldDelay - remainder + newRemainder;
                    flashModeProgressBar.setMax(flashModeCurrentPageDelay);

                    flashChanged = false;
                }

                if (flashModeCurrentTime == 0) {
                    currentPage = separatedBook.getPage(currentPageIndex);

                    textView.setTextColor(Color.BLACK);
                    textView.setText(currentPage);

                    flashModeCurrentPageDelay = getFlashModeReadingDelay(currentPage, currentSpeedArray[speedIndex]);
                    flashModeProgressBar.setMax(flashModeCurrentPageDelay);
                } else if (flashModeCurrentTime > flashModeCurrentPageDelay - flashModeCurrentPageDelay / 10) {
                    textView.setTextColor(Color.GRAY);
                }

                flashModeCurrentTime += FLASH_MODE_PROGRESS_DELAY;
                flashModeProgressBar.setProgress(flashModeCurrentTime);

                if (flashModeCurrentTime >= flashModeCurrentPageDelay) {
                    flashModeCurrentTime = 0;

                    if (currentPageIndex < separatedBook.size() - 1) {
                        currentPageIndex++;
                        currentPageResultTextView.setText(getCurrentPageByString());

                        textView.post(this);
                    } else {
                        fastReadingStarted = false;
                    }
                } else {
                    textView.postDelayed(this, FLASH_MODE_PROGRESS_DELAY);
                }
            }
        }
    };

    private View.OnTouchListener readingListenerOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();

                if (x < thirdPartOfPageWidth) {
                    itsSeekPause = true;
                    navigationLayout.setVisibility(View.INVISIBLE);

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
                        navigationLayout.setVisibility(View.INVISIBLE);

                    }
                } else {
                    itsSeekPause = true;
                    navigationLayout.setVisibility(View.INVISIBLE);

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
                    if (speedIndex > 0){
                        speedIndex--;
                        speedResultTextView.setText(String.valueOf(currentSpeedArray[speedIndex]));
                    }
                } else if (thirdPartOfPageWidth <= x && x <= pageWidth - thirdPartOfPageWidth){
                    fastReadingStarted = ! fastReadingStarted;

                    if (fastReadingStarted) {
                        navigationLayout.setVisibility(View.INVISIBLE);

                        if (currentPageIndex !=  separatedBook.size() - 1) {
                            textView.post(fastReadingThread);
                        } else {
                            if (wordSelector != null) {
                                textView.post(fastReadingThread);
                            }
                        }

                    } else {
                        currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
                        seekBar.setMax(separatedBook.size() - 1);
                        seekBar.setProgress(currentPageIndex);
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        navigationLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (speedIndex < currentSpeedArray.length - 1){
                        speedIndex++;
                        speedResultTextView.setText(String.valueOf(currentSpeedArray[speedIndex]));
                    } else {
                        if (! isPremiumUser) {
                            fastReadingStarted = false;
                            purchasePremium();
                        }
                    }
                }
            }

            return false;
        }
    };

    private View.OnTouchListener flashReadingOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();

                if (x < thirdPartOfPageWidth) {
                    if (speedIndex > 0){
                        speedIndex--;
                        speedResultTextView.setText(String.valueOf(currentSpeedArray[speedIndex]));

                        flashChanged = true;
                    }
                } else if (thirdPartOfPageWidth <= x && x <= pageWidth - thirdPartOfPageWidth){

                    fastReadingStarted = ! fastReadingStarted;

                    if (fastReadingStarted) {
                        navigationLayout.setVisibility(View.INVISIBLE);

                        if (currentPageIndex != separatedBook.size() - 1) {
                            textView.post(flashReadingThread);
                        } else {
                            if (flashModeCurrentTime != 0) {
                                textView.post(flashReadingThread);
                            }
                        }
                    } else {
                        currentChapterTitleTextView.setText(separatedBook.getTitle(currentPageIndex));
                        seekBar.setMax(separatedBook.size() - 1);
                        seekBar.setProgress(currentPageIndex);
                        currentPageSeekTextView.setText(getCurrentPageByString());
                        navigationLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (speedIndex < currentSpeedArray.length - 1){
                        speedIndex++;
                        speedResultTextView.setText(String.valueOf(currentSpeedArray[speedIndex]));

                        flashChanged = true;
                    } else {
                        if (! isPremiumUser) {
                            fastReadingStarted = false;
                            purchasePremium();
                        }
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

    private SeekBar.OnSeekBarChangeListener flashReadingSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
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
            textView.removeCallbacks(flashReadingThread);

            flashModeCurrentTime = 0;
            flashModeProgressBar.setProgress(0);

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
