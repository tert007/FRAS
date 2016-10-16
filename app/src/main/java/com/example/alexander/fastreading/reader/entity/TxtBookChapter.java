package com.example.alexander.fastreading.reader.entity;

/**
 * Created by Alexander on 16.10.2016.
 */
public class TxtBookChapter implements BookChapter {

    private CharSequence title;
    private CharSequence content;

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public CharSequence getContent() {
        return content;
    }

    @Override
    public CharSequence getBookChapter() {
        return content;
    }
}
