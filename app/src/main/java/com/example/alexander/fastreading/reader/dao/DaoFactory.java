package com.example.alexander.fastreading.reader.dao;

import android.content.Context;

/**
 * Created by Alexander on 03.09.2016.
 */
public abstract class DaoFactory {

    public static DaoFactory getDaoFactory(Context context){
        return new SqlLiteDaoFactory(context);
    }

    public abstract BookDescriptionDao getBookDescriptionDao();
}
