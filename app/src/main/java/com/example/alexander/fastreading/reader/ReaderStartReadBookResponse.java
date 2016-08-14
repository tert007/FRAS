package com.example.alexander.fastreading.reader;

import com.example.alexander.fastreading.reader.book.Book;

/**
 * Created by Alexander on 08.08.2016.
 * Передает  книгу из фрагмента в активити
 */
public interface ReaderStartReadBookResponse {
    void onBookClick(Book book);
}
