package com.example.alexander.fastreading.reader.dao.bookdescription;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Alexander on 03.09.2016.
 */
public class SQLiteDaoFactory extends BookDescriptionDaoFactory {

    private SQLiteDatabase database;

    public SQLiteDaoFactory(Context context){
        BookDescriptionDatabaseHelper bookDescriptionDatabaseHelper = new BookDescriptionDatabaseHelper(context);

        this.database = bookDescriptionDatabaseHelper.getWritableDatabase();
    }


    @Override
    public BookDescriptionDao getBookDescriptionDao() {
        return new SqlLiteBookDescriptionDao(database);
    }
}
