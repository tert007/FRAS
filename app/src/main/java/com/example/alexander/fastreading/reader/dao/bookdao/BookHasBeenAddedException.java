package com.example.alexander.fastreading.reader.dao.bookdao;

/**
 * Created by Alexander on 29.09.2016.
 */
public class BookHasBeenAddedException extends Exception {
    public BookHasBeenAddedException() {
        super();
    }

    public BookHasBeenAddedException(String message) {
        super(message);
    }

    public BookHasBeenAddedException(String message, Throwable cause) {
        super(message, cause);
    }
}
