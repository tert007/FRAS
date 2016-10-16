package com.example.alexander.fastreading.reader.dao.bookdao;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.dao.bookdescriptiondao.BookDescriptionDao;
import com.example.alexander.fastreading.reader.entity.BookChapter;
import com.example.alexander.fastreading.reader.entity.BookContent;
import com.example.alexander.fastreading.reader.entity.BookDescription;
import com.example.alexander.fastreading.reader.entity.TxtBookChapter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alexander on 16.10.2016.
 */
public class TxtBookDao extends AbstractBookDao implements BookDao {

    public TxtBookDao(BookDescriptionDao bookDescriptionDao) {
        super(bookDescriptionDao);
    }

    @Override
    public BookDescription addBook(String filePath) throws BookParserException, BookHasBeenAddedException {
        if (bookDescriptionDao.findBookDescription(filePath) != null) {
            throw new BookHasBeenAddedException("The book has been added");
        }

        BookDescription bookDescription = new BookDescription();

        long id = bookDescriptionDao.getNextItemId();

        bookDescription.setId(id);
        bookDescription.setFilePath(filePath);
        bookDescription.setType(FileHelper.getFileExtension(filePath));
        bookDescription.setTitle(FileHelper.getFileName(filePath));

        bookDescriptionDao.addBookDescription(bookDescription);

        return bookDescription;
    }

    @Override
    public BookContent getBookContent(BookDescription bookDescription) throws BookParserException {

        TxtBookChapter bookChapter = new TxtBookChapter();

        try {
            String encoding = FileHelper.getEncoding(new File(bookDescription.getFilePath()));
            if (encoding == null) {
                String content = FileHelper.getTextFromFile(bookDescription.getFilePath());
                bookChapter.setTitle(bookDescription.getTitle());
                bookChapter.setContent(content);
            } else {
                String content = FileHelper.getTextFromFile(bookDescription.getFilePath(), encoding);
                bookChapter.setTitle(bookDescription.getTitle());
                bookChapter.setContent(content);
            }

            List<BookChapter> bookChapterList = new LinkedList<>();
            bookChapterList.add(bookChapter);

            BookContent bookContent = new BookContent();
            bookContent.setBookChapterList(bookChapterList);

            return bookContent;
        } catch (IOException e) {
            throw new BookParserException(e);
        }
    }

    @Override
    public void removeBook(long id) {
        bookDescriptionDao.removeBookDescription(id);
    }
}
