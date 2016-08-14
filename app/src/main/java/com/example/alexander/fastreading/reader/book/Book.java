package com.example.alexander.fastreading.reader.book;

/**
 * Created by Alexander on 05.08.2016.
 */
public abstract class Book {
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
