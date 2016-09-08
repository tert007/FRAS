package com.example.alexander.fastreading.reader.dao.bookdescriptiondao;

import android.content.Context;

/**
 * Created by Alexander on 05.09.2016.
 */
public abstract class BookDescriptionDaoFactory {

    public static BookDescriptionDaoFactory getDaoFactory(Context context){
        return new SQLiteDaoFactory(context);
    }

    public abstract BookDescriptionDao getBookDescriptionDao();
}
