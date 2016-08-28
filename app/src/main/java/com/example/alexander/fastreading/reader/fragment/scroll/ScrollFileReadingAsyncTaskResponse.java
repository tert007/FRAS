package com.example.alexander.fastreading.reader.fragment.scroll;

import android.text.Spanned;

/**
 * Created by Alexander on 27.08.2016.
 */
public interface ScrollFileReadingAsyncTaskResponse {
    void onFileReadingPostExecute(Spanned spannedText);
}
