package com.example.alexander.fastreading.reader.entity;

import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 15.09.2016.
 */
public class BookContent {

    List<BookChapter> chapters;

    public BookContent() {
        this.chapters = new ArrayList<>();
    }

    public List<BookChapter> getChapters() {
        return chapters;
    }

    public void addBookChapter(BookChapter bookChapter) {
        chapters.add(bookChapter);
    }

    public CharSequence getScrollContent() {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (BookChapter chapter : chapters) {
            builder.append(recursive(chapter));
        }

        return builder;
    }

    public List<CharSequence> getChaptersText() {
        List<CharSequence> result = new ArrayList<>();

        for (BookChapter chapter : chapters) {
            result.add(recursive(chapter));
        }

        return result;
    }

    private CharSequence recursive(BookChapter bookChapter){
        SpannableStringBuilder builder = new SpannableStringBuilder();

        CharSequence title = bookChapter.getTitle();
        CharSequence epigraph = bookChapter.getEpigraph();
        CharSequence content = bookChapter.getContent();

        if (title != null) {
            builder.append(title);
        }

        if (epigraph != null) {
            builder.append(epigraph);
        }

        if (content != null) {
            builder.append(content);
        }

        for (BookChapter chapter : bookChapter.getChildChapters()) {
            builder.append(recursive(chapter));
        }

        return builder;
    }
}
