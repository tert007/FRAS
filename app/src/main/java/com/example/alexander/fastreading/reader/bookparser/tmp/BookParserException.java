package com.example.alexander.fastreading.reader.bookparser.tmp;

/**
 * Created by Alexander on 06.08.2016.
 */
public class BookParserException extends Exception {
    public BookParserException() {
    }

    public BookParserException(String message) {
        super(message);
    }

    public BookParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookParserException(Throwable cause) {
        super(cause);
    }
}
