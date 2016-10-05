package com.example.alexander.fastreading.reader.dao.bookdao;

import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;

/**
 * Created by Alexander on 29.09.2016.
 */
public interface BookDao {
    BookDescription addBook(String filePath) throws BookParserException, BookHasBeenAddedException;
    BookContent getBookContent(BookDescription bookDescription) throws BookParserException;
    void removeBook(long id);
}
