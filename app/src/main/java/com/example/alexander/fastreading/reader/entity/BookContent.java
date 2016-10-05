package com.example.alexander.fastreading.reader.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 15.09.2016.
 */
public class BookContent {

    private List<BookChapter> bookChapterList;

    public List<BookChapter> getBookChapterList() {
        return bookChapterList;
    }

    public void setBookChapterList(List<BookChapter> bookChapterList) {
        this.bookChapterList = bookChapterList;
    }

    public List<CharSequence> getChaptersText() {
        final List<CharSequence> chapterTextList = new ArrayList<>(bookChapterList.size());

        for (BookChapter chapter : bookChapterList) {
            chapterTextList.add(chapter.getBookChapter());
        }

        return chapterTextList;
    }

    public List<String> getTitles() {
        final List<String> titleList = new ArrayList<>(bookChapterList.size());

        for (BookChapter bookChapter : bookChapterList) {
            titleList.add(bookChapter.getTitle().toString());
        }
        
        return titleList;
    }
}
