package com.example.alexander.fastreading.reader.bookparser.trash;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.bookparser.BookParserException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alexander on 06.08.2016.
 */
public class TxtBookParser implements BookParser {
    @Override
    public Book getBook(File fileBook) throws BookParserException {

        try {
            TxtBook txtBook = new TxtBook();

            txtBook.setFilePath(fileBook.getPath());
            txtBook.setText(FileHelper.getTextFromFile(fileBook));

            return txtBook;
        } catch (IOException e){
            throw new BookParserException(e);
        }

    }
}
