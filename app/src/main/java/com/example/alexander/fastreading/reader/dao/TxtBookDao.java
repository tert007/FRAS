package com.example.alexander.fastreading.reader.dao;

import android.content.Context;

import com.example.alexander.fastreading.reader.bookparser.BookDescription;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserException;

import java.util.List;

/**
 * Created by Alexander on 07.09.2016.
 */
public class TxtBookDao implements BookDao {

    private Context context;

    public TxtBookDao(Context context) {
        this.context = context;
    }

    @Override
    public BookDescription addBook(String filePath) throws BookParserException {
        return null;
    }

    @Override
    public void removeBook(long id) throws BookParserException {

    }

    @Override
    public List<BookDescription> getBookDescriptions() throws BookParserException {
        return null;
    }

    @Override
    public CharSequence getScrollText(long bookId) throws BookParserException {
        return null;
    }

    @Override
    public List<CharSequence> getChaptersText(long bookId) throws BookParserException {
        return null;
    }
}
