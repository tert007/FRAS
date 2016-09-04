package com.example.alexander.fastreading.reader.bookparser.trash;



import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.bookparser.trash.Book;
import com.example.alexander.fastreading.reader.bookparser.trash.BookParser;
import com.example.alexander.fastreading.reader.bookparser.BookParserException;
import com.example.alexander.fastreading.reader.bookparser.trash.EpubBookParser;
import com.example.alexander.fastreading.reader.bookparser.trash.TxtBookParser;

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
