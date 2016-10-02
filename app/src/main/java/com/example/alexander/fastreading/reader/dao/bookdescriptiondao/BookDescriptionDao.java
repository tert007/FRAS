package com.example.alexander.fastreading.reader.dao.bookdescriptiondao;

import com.example.alexander.fastreading.reader.dao.bookdao.BookHasBeenAddedException;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 29.09.2016.
 */
public interface BookDescriptionDao {
    long addBookDescription(BookDescription bookDescription) throws BookHasBeenAddedException;

    void updateBookDescription(BookDescription bookDescription);

    BookDescription findBookDescription(long id);
    BookDescription findBookDescription(String filePath);

    void removeBookDescription(long id);

    List<BookDescription> getBookDescriptions();

    long getNextItemId();
}
