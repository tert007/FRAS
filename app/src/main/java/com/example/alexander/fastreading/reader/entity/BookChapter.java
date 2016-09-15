package com.example.alexander.fastreading.reader.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 15.09.2016.
 */
public class BookChapter {
    private CharSequence title;
    private CharSequence epigraph;
    private CharSequence content;

    private List<BookChapter> childChapters;

    public BookChapter(CharSequence title, CharSequence epigraph, CharSequence content) {
        this.title = title;
        this.epigraph = epigraph;
        this.content = content;
        this.childChapters = new ArrayList<>();
    }

    public BookChapter(){
        this.childChapters = new ArrayList<>();
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public List<BookChapter> getChildChapters() {
        return childChapters;
    }

    public void addChildChapter(BookChapter bookChapter) {
        childChapters.add(bookChapter);
    }

    public CharSequence getContent() {
        return content;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    public CharSequence getEpigraph() {
        return epigraph;
    }

    public void setEpigraph(CharSequence epigraph) {
        this.epigraph = epigraph;
    }
}
