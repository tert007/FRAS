package com.example.alexander.fastreading.reader.fragment.scroll;

import android.text.Spanned;

import com.example.alexander.fastreading.reader.entity.BookContent;

/**
 * Created by Alexander on 27.08.2016.
 */
public interface ScrollFileReadingAsyncTaskResponse {
    void fileReadingPostExecute(CharSequence result);
}
