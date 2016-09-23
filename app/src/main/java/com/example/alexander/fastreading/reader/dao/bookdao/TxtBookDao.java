package com.example.alexander.fastreading.reader.dao.bookdao;

import android.content.Context;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 07.09.2016.
 */
public class TxtBookDao implements BookDao {

    private BookDescriptionDao bookDescriptionDao;

    public TxtBookDao(Context context) {
        bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(context).getBookDescriptionDao();
    }

    @Override
    public BookDescription addBook(String filePath) throws BookParserException {
        BookDescription bookDescription = createBookDescription(filePath);

        long id = bookDescriptionDao.addBookDescription(bookDescription);
        if (id == -1)
            return null;

        bookDescription.setId(id);

        return bookDescription;
    }

    private BookDescription createBookDescription(String filePath) {
        BookDescription bookDescription = new BookDescription();

        bookDescription.setFilePath(filePath);
        bookDescription.setTitle(FileHelper.getFileName(filePath));
        bookDescription.setType(FileHelper.getFileExtension(filePath));

        return bookDescription;
    }

    @Override
    public void removeBook(BookDescription bookDescription) {
        bookDescriptionDao.removeBookDescription(bookDescription.getId());
    }

    @Override
    public CharSequence getScrollText(BookDescription bookDescription) throws BookParserException {
        try {
            return FileHelper.getTextFromFile(bookDescription.getFilePath());
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }

    @Override
    public List<CharSequence> getChaptersText(BookDescription bookDescription) throws BookParserException {
        try {
            List<CharSequence> result = new ArrayList<>();

            result.add(FileHelper.getTextFromFile(bookDescription.getFilePath()));

            return result;
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }
}