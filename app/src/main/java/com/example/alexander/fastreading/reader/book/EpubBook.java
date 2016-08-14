package com.example.alexander.fastreading.reader.book;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Alexander on 05.08.2016.
 */
public class EpubBook extends Book {

    private String title;
    private String author;
    private String language;
    private Bitmap cover;
    private List<BookNavigationPoint> navigationPoints;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public List<BookNavigationPoint> getNavigationPoints() {
        return navigationPoints;
    }

    public void setNavigationPoints(List<BookNavigationPoint> navigationPoints) {
        this.navigationPoints = navigationPoints;
    }

}
