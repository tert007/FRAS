package com.example.alexander.fastreading.reader.dao.bookdao;

import com.example.alexander.fastreading.reader.entity.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 05.09.2016.
 */
public interface BookDao {
    BookDescription addBook(String filePath) throws BookParserException;
    void removeBook(BookDescription bookDescription);

    CharSequence getScrollText(BookDescription bookDescription) throws BookParserException;
    List<CharSequence> getChaptersText(BookDescription bookDescription) throws BookParserException;
}
