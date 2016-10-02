package com.example.alexander.fastreading.reader.entity;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 15.09.2016.
 */
public class BookContent {

    List<BookChapter> bookChapterList;

    public List<BookChapter> getBookChapterList() {
        return bookChapterList;
    }

    public void setBookChapterList(List<BookChapter> bookChapterList) {
        this.bookChapterList = bookChapterList;
    }

    public List<CharSequence> getChaptersText() {
        final List<CharSequence> result = new ArrayList<>(bookChapterList.size());

        for (BookChapter chapter : bookChapterList) {
            result.add(chapter.getBookChapter());
        }

        return result;
    }
}
