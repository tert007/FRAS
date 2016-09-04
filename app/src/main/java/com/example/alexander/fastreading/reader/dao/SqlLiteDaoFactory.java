package com.example.alexander.fastreading.reader.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Alexander on 03.09.2016.
 */
public class SqlLiteDaoFactory extends DaoFactory {

    //private Context context;
    private SQLiteDatabase database;
    private BookDatabaseHelper bookDatabaseHelper ;

    public SqlLiteDaoFactory(Context context){
        //this.context = context;
        this.bookDatabaseHelper = new BookDatabaseHelper(context);
        this.database = bookDatabaseHelper.getWritableDatabase();
    }

    @Override
    public BookDescriptionDao getBookDescriptionDao() {
        return new SqlLiteBookDescriptionDao(database);
    }
}
