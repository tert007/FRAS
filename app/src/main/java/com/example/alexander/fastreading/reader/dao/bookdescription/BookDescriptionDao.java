package com.example.alexander.fastreading.reader.dao.bookdescription;

import com.example.alexander.fastreading.reader.dao.BookParserException;
import com.example.alexander.fastreading.reader.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 05.09.2016.
 */
public interface BookDescriptionDao {
    long addBookDescription(BookDescription bookDescription) throws BookParserException;
    BookDescription findBookDescription(long id) throws BookParserException;
    void removeBookDescription(long id) throws BookParserException;
    long updateBookDescription(BookDescription newValue) throws BookParserException;

    List<BookDescription> getBookDescriptions() throws BookParserException;
}