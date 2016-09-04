package com.example.alexander.fastreading.reader.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.alexander.fastreading.reader.bookparser.BookDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 03.09.2016.
 */
public class SqlLiteBookDescriptionDao implements BookDescriptionDao {

    private SQLiteDatabase database;

    public SqlLiteBookDescriptionDao(SQLiteDatabase database) {
        this.database = database;
    }

    private ContentValues getBookValues(BookDescription bookDescription){
        ContentValues values = new ContentValues();
        values.put(BookDatabaseHelper.BOOK_FILE_PATH, bookDescription.getFilePath());
        values.put(BookDatabaseHelper.BOOK_TYPE, bookDescription.getType());
        values.put(BookDatabaseHelper.BOOK_FAVORITE_FLAG, bookDescription.itsFavorite());
        values.put(BookDatabaseHelper.BOOK_READING_PROGRESS, bookDescription.getProgress());
        return values;
    }

    @Override
    public long add(BookDescription bookDescription) {
        return database.insert(BookDatabaseHelper.BOOK_TABLE, null, getBookValues(bookDescription));
    }

    @Override
    public List<BookDescription> getBookDescriptions() {
        Cursor cursor = database.query(BookDatabaseHelper.BOOK_TABLE, BookDatabaseHelper.BOOK_TABLE_COLUMNS, null, null, null, null, null);
        List<BookDescription> bookDescriptions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                bookDescriptions.add(CursorToGoal(cursor));
            } while (cursor.moveToNext());
        }

        return bookDescriptions;
    }

    private BookDescription CursorToGoal(Cursor cursor){
        BookDescription bookDescription = new BookDescription();

        bookDescription.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
        bookDescription.setFilePath(cursor.getString(cursor.getColumnIndex(BookDatabaseHelper.BOOK_FILE_PATH)));
        /*
        goal.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        goal.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        goal.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        goal.setPriority(cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY)));
        goal.setDate(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))));
        goal.setPeriod(cursor.getInt(cursor.getColumnIndex(COLUMN_PERIOD)));
        */
        return bookDescription;
    }
}
