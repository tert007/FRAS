package com.example.alexander.fastreading.reader.fragment.description;


import com.example.alexander.fastreading.reader.BookDescription;

/**
 * Created by Alexander on 08.08.2016.
 * Передает  книгу из фрагмента в активити
 */
public interface ReaderScrollReadBookResponse {
    void onScrollReadBookClick(BookDescription bookDescription);
}
