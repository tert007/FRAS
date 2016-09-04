package com.example.alexander.fastreading.reader.bookparser;

import android.graphics.Bitmap;

/**
 * Created by Alexander on 03.09.2016.
 */
public class BookDescription {
    private int id;

    private float progress;

    private String title;
    private String author;
    private String language;

    private String type;

    private String filePath;

    private boolean itsFavorite;

    private Bitmap cover;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean itsFavorite() {
        return itsFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.itsFavorite = favorite;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }


    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

}
