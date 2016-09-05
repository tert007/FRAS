package com.example.alexander.fastreading.reader.dao.bookdescription;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Alexander on 02.09.2016.
 */
public class BookDescriptionDatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    private static final String DATABASE_NAME = "books_database.db";
    private static final int DATABASE_VERSION = 1;

    ////////////////////////////////////////////////////
    public static final String BOOK_TABLE = "books";

    public static final String BOOK_FILE_PATH = "file_path";
    public static final String BOOK_TYPE = "type";
    public static final String BOOK_FAVORITE_FLAG = "favorite";
    public static final String BOOK_READING_PROGRESS = "progress";

    public static final String[] BOOK_TABLE_COLUMNS = new String[]{
            BaseColumns._ID,
            BOOK_FILE_PATH,
            BOOK_TYPE,
            BOOK_FAVORITE_FLAG,
            BOOK_READING_PROGRESS
    };

    ////////////////////////////////////////////////////
    public static final String E_BOOK_TABLE = "e_books";

    public static final String E_BOOK_ID = "id";
    public static final String E_BOOK_TITLE = "title";
    public static final String E_BOOK_AUTHOR = "author";
    public static final String E_BOOK_LANGUAGE = "language";
    public static final String E_BOOK_COVER_NAME = "cover_name";

    public static final String[] E_BOOK_TABLE_COLUMNS = new String[]{
            E_BOOK_ID,
            E_BOOK_TITLE,
            E_BOOK_AUTHOR,
            E_BOOK_LANGUAGE,
            E_BOOK_COVER_NAME
    };


    public BookDescriptionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " +
                        BOOK_TABLE + "(" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        BOOK_FILE_PATH + " TEXT NOT NULL, " +
                        BOOK_TYPE + " TEXT NOT NULL, " +
                        BOOK_FAVORITE_FLAG + " INTEGER NOT NULL, " +
                        BOOK_READING_PROGRESS + " REAL NOT NULL);"
        );

        db.execSQL(
                "CREATE TABLE " +
                        E_BOOK_TABLE + "(" +
                        E_BOOK_ID + " INTEGER, " +
                        E_BOOK_TITLE + " TEXT, " +
                        E_BOOK_AUTHOR + " TEXT, " +
                        E_BOOK_LANGUAGE + " TEXT, " +
                        E_BOOK_COVER_NAME + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + E_BOOK_TABLE);
        onCreate(db);
    }
}
