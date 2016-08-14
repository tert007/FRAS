package com.example.alexander.fastreading.reader.bookparser;

import com.example.alexander.fastreading.reader.book.Book;

import java.io.File;

/**
 * Created by Alexander on 05.08.2016.
 */
public interface BookParser {
    Book getBook(File fileBook) throws BookParserException;
}
