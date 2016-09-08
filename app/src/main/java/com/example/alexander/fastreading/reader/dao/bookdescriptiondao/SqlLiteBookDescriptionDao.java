package com.example.alexander.fastreading.reader.dao.bookdescriptiondao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.alexander.fastreading.reader.dao.bookdao.BookParserException;
import com.example.alexander.fastreading.reader.entity.BookDescription;

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

    private ContentValues getValues(BookDescription bookDescription){
        ContentValues values = new ContentValues();
        values.put(BookDescriptionDatabaseHelper.BOOK_TITLE, bookDescription.getTitle());
        values.put(BookDescriptionDatabaseHelper.BOOK_AUTHOR, bookDescription.getAuthor());
        values.put(BookDescriptionDatabaseHelper.BOOK_LANGUAGE, bookDescription.getLanguage());
        values.put(BookDescriptionDatabaseHelper.BOOK_COVER_NAME, bookDescription.getCoverImagePath());
        values.put(BookDescriptionDatabaseHelper.BOOK_FILE_PATH, bookDescription.getFilePath());
        values.put(BookDescriptionDatabaseHelper.BOOK_TYPE, bookDescription.getType());
        values.put(BookDescriptionDatabaseHelper.BOOK_FAVORITE_FLAG, bookDescription.itsFavorite());
        values.put(BookDescriptionDatabaseHelper.BOOK_READING_PROGRESS, bookDescription.getProgress());
        return values;
    }

    @Override
    public long addBookDescription(BookDescription bookDescription) throws BookParserException {
        return database.insert(BookDescriptionDatabaseHelper.BOOK_TABLE, null, getValues(bookDescription));
    }

    @Override
    public BookDescription findBookDescription(long id) throws BookParserException {
        Cursor cursor = database.rawQuery("SELECT * FROM " + BookDescriptionDatabaseHelper.BOOK_TABLE + " WHERE " + BaseColumns._ID + "='" + id + "'" , null);
        if (cursor.moveToFirst()) {
            return getBookDescription(cursor);
        }

        return null;
    }

    @Override
    public long updateBookDescription(BookDescription newValue) throws BookParserException {
        return database.update(BookDescriptionDatabaseHelper.BOOK_TABLE, getValues(newValue), BaseColumns._ID + "=" + newValue.getId(), null);
    }

    @Override
    public void removeBookDescription(long id) throws BookParserException {

    }



    @Override
    public List<BookDescription> getBookDescriptions() {
        Cursor cursor = database.query(BookDescriptionDatabaseHelper.BOOK_TABLE, BookDescriptionDatabaseHelper.BOOK_TABLE_COLUMNS, null, null, null, null, null);
        List<BookDescription> bookDescriptions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                bookDescriptions.add(getBookDescription(cursor));
            } while (cursor.moveToNext());
        }

        return bookDescriptions;
    }

    private BookDescription getBookDescription(Cursor cursor){
        BookDescription bookDescription = new BookDescription();

        bookDescription.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));

        bookDescription.setTitle(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_TITLE)));
        bookDescription.setAuthor(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_AUTHOR)));
        bookDescription.setLanguage(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_LANGUAGE)));
        bookDescription.setCoverImagePath(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_COVER_NAME)));

        bookDescription.setFilePath(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_FILE_PATH)));
        bookDescription.setType(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_TYPE)));
        bookDescription.setProgress(cursor.getFloat(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_READING_PROGRESS)));

        int favorite = cursor.getInt(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_FAVORITE_FLAG));
        boolean favoriteFlag = favorite == 1;

        bookDescription.setFavorite(favoriteFlag);

        return bookDescription;
    }

}
