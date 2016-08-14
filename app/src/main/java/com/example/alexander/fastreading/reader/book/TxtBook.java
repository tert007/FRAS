package com.example.alexander.fastreading.reader.book;

/**
 * Created by Alexander on 13.08.2016.
 */
public class TxtBook extends Book {
    private String text;

    public TxtBook(){
    }

    public TxtBook(String text) {
        this.text = text;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
