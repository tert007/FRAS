package com.example.alexander.fastreading.reader.dao.bookdescriptiondao;

import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.entity.BookDescription;

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