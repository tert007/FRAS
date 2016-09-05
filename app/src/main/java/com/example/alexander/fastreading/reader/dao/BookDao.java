package com.example.alexander.fastreading.reader.dao;

import com.example.alexander.fastreading.reader.bookparser.BookDescription;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserException;

import java.util.List;

/**
 * Created by Alexander on 05.09.2016.
 */
public interface BookDao {
    long addBook(String filePath) throws BookParserException;
    void removeBook(long id) throws BookParserException;

    List<BookDescription> getBookDescriptions() throws BookParserException;

    CharSequence getScrollText(int bookId) throws BookParserException;
    List<CharSequence> getPagesText(int bookId) throws BookParserException;
}
