package com.example.alexander.fastreading.reader;



import com.example.alexander.fastreading.reader.book.Book;
import com.example.alexander.fastreading.reader.bookparser.BookParser;
import com.example.alexander.fastreading.reader.bookparser.BookParserException;
import com.example.alexander.fastreading.reader.bookparser.EpubBookParser;
import com.example.alexander.fastreading.reader.bookparser.TxtBookParser;

import java.io.File;

/**
 * Created by Alexander on 06.08.2016.
 */
public class BookReaderHelper {
    public static Book getBook(File file) throws BookParserException {
        BookParser bookParser = getBookParser(file);

        return bookParser.getBook(file);
    }

    private static BookParser getBookParser(File file) {
        String fileExtension = FileHelper.getFileExtension(file);

        switch (fileExtension){
            case FileHelper.TXT:
                return new TxtBookParser();
            case FileHelper.EPUB:
                return new EpubBookParser();
            default:
                return null;
        }
    }
}
