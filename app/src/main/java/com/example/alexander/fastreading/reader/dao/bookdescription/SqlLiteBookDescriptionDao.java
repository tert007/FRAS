package com.example.alexander.fastreading.reader.dao.bookdescription;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.alexander.fastreading.reader.FileHelper;
import com.example.alexander.fastreading.reader.bookparser.tmp.BookParserException;
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
        values.put(BookDescriptionDatabaseHelper.BOOK_FILE_PATH, bookDescription.getFilePath());
        values.put(BookDescriptionDatabaseHelper.BOOK_TYPE, bookDescription.getType());
        values.put(BookDescriptionDatabaseHelper.BOOK_FAVORITE_FLAG, bookDescription.itsFavorite());
        values.put(BookDescriptionDatabaseHelper.BOOK_READING_PROGRESS, bookDescription.getProgress());
        return values;
    }

    private ContentValues getEBookValues(BookDescription bookDescription){
        ContentValues values = new ContentValues();
        values.put(BookDescriptionDatabaseHelper.E_BOOK_ID, bookDescription.getId());
        values.put(BookDescriptionDatabaseHelper.E_BOOK_TITLE, bookDescription.getTitle());
        values.put(BookDescriptionDatabaseHelper.E_BOOK_AUTHOR, bookDescription.getAuthor());
        values.put(BookDescriptionDatabaseHelper.E_BOOK_LANGUAGE, bookDescription.getLanguage());
        values.put(BookDescriptionDatabaseHelper.E_BOOK_COVER_NAME, bookDescription.getCoverImageName());
        return values;
    }

    @Override
    public long addBookDescription(BookDescription bookDescription) throws BookParserException {
        if (bookDescription.getType().equals(FileHelper.EPUB)){
            database.insert(BookDescriptionDatabaseHelper.E_BOOK_TABLE, null, getEBookValues(bookDescription));
        }
        return database.insert(BookDescriptionDatabaseHelper.BOOK_TABLE, null, getBookValues(bookDescription));
    }

    @Override
    public long updateBookDescription(BookDescription newValue) throws BookParserException {
        if (newValue.getType().equals(FileHelper.EPUB)){
            database.insert(BookDescriptionDatabaseHelper.E_BOOK_TABLE, null, getEBookValues(newValue));
        }
        return database.update(BookDescriptionDatabaseHelper.BOOK_TABLE, getBookValues(newValue), BaseColumns._ID + "=" + newValue.getId(), null);
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

        cursor = database.query(BookDescriptionDatabaseHelper.E_BOOK_TABLE, BookDescriptionDatabaseHelper.E_BOOK_TABLE_COLUMNS, null, null, null, null, null);
        if (cursor.moveToFirst()){

            for (BookDescription bookDescription : bookDescriptions) {
                if (bookDescription.getType().equals(FileHelper.EPUB)){
                    getEBookDescription(cursor, bookDescription);
                    if (!cursor.moveToNext())
                        break;
                }
            }
        }

        return bookDescriptions;
    }

    private BookDescription getBookDescription(Cursor cursor){
        BookDescription bookDescription = new BookDescription();

        bookDescription.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
        bookDescription.setFilePath(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_FILE_PATH)));
        bookDescription.setType(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_TYPE)));
        bookDescription.setProgress(cursor.getFloat(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_READING_PROGRESS)));

        int favorite = cursor.getInt(cursor.getColumnIndex(BookDescriptionDatabaseHelper.BOOK_FAVORITE_FLAG));
        boolean favoriteFlag = favorite == 1;

        bookDescription.setFavorite(favoriteFlag);

        return bookDescription;
    }

    private BookDescription getEBookDescription(Cursor cursor, BookDescription bookDescription){

        bookDescription.setTitle(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.E_BOOK_TITLE)));
        bookDescription.setAuthor(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.E_BOOK_AUTHOR)));
        bookDescription.setLanguage(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.E_BOOK_LANGUAGE)));
        bookDescription.setCoverImageName(cursor.getString(cursor.getColumnIndex(BookDescriptionDatabaseHelper.E_BOOK_COVER_NAME)));

        return bookDescription;
    }
}
