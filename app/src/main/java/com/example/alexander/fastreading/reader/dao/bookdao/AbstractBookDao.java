package com.example.alexander.fastreading.reader.dao.bookdao;

import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;

/**
 * Created by Alexander on 29.09.2016.
 */
public abstract class AbstractBookDao implements BookDao {
    protected BookDescriptionDao bookDescriptionDao;

    public AbstractBookDao(BookDescriptionDao bookDescriptionDao) {
        this.bookDescriptionDao = bookDescriptionDao;
    }
}
