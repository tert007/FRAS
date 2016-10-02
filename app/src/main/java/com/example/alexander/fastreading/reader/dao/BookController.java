package com.example.alexander.fastreading.reader.dao;

import android.content.Context;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.dao.bookdao.BookHasBeenAddedException;
import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.dao.bookdao.EpubBookDao;
import com.example.alexander.fastreading.reader.dao.bookdao.Fb2BookDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDaoFactory;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;

import java.util.List;

/**
 * Created by Alexander on 29.09.2016.
 */
public class BookController {
    private Context context;
    private BookDescriptionDao bookDescriptionDao;

    private Fb2BookDao fb2BookDao;
    private EpubBookDao epubBookDao;

    public BookController(Context context) {
        this.context = context;
        this.bookDescriptionDao = BookDescriptionDaoFactory.getDaoFactory(context).getBookDescriptionDao();
    }

    public BookDescription addBook(String filePath) throws BookParserException, BookHasBeenAddedException {
        switch (FileHelper.getFileExtension(filePath)) {
            case FileHelper.FB2:
                if (fb2BookDao == null)
                    fb2BookDao = new Fb2BookDao(bookDescriptionDao, context);

                return fb2BookDao.addBook(filePath);
            case FileHelper.EPUB:
                if (epubBookDao == null)
                    epubBookDao = new EpubBookDao(bookDescriptionDao, context);

                return epubBookDao.addBook(filePath);
        }

        return null;
    }


    public void removeBook(BookDescription bookDescription) {
        switch (bookDescription.getType()) {
            case FileHelper.FB2:
                if (fb2BookDao == null)
                    fb2BookDao = new Fb2BookDao(bookDescriptionDao, context);

                fb2BookDao.removeBook(bookDescription.getId());
            case FileHelper.EPUB:
                if (epubBookDao == null)
                    epubBookDao = new EpubBookDao(bookDescriptionDao, context);

                epubBookDao.removeBook(bookDescription.getId());
        }
    }


    public BookDescription findBookDescription(String filePath) {
        return bookDescriptionDao.findBookDescription(filePath);
    }

    public BookContent getBookContent(String filePath) throws BookParserException {
        switch (FileHelper.getFileExtension(filePath)) {
            case FileHelper.FB2:
                if (fb2BookDao == null)
                    fb2BookDao = new Fb2BookDao(bookDescriptionDao, context);

                return fb2BookDao.getBookContent(filePath);
            case FileHelper.EPUB:
                if (epubBookDao == null)
                    epubBookDao = new EpubBookDao(bookDescriptionDao, context);

                return epubBookDao.getBookContent(filePath);
        }

        return null;
    }

    public List<BookDescription> getBookDescriptions() {
        return bookDescriptionDao.getBookDescriptions();
    }

    public void updateBookDescription(BookDescription bookDescription) {
        bookDescriptionDao.updateBookDescription(bookDescription);
    }
}
