package com.example.alexander.fastreading.reader.dao;

import com.example.alexander.fastreading.reader.bookparser.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 03.09.2016.
 */
public interface BookDescriptionDao {
    List<BookDescription> getBookDescriptions();
    long add(BookDescription bookDescription);

}
