package com.example.alexander.fastreading.reader.dao.bookdao;

import android.content.Context;

import com.example.alexander.fastreading.reader.FileHelper;

/**
 * Created by Alexander on 07.09.2016.
 */
public class BookDaoFactory {

    private Context context;

    public BookDaoFactory(Context context){
        this.context = context;
    }

    public BookDao getBookDao(String bookType) {
        switch (bookType){
            case FileHelper.EPUB:
                return new EpubBookDao(context);
            case FileHelper.TXT:
                return new TxtBookDao(context);
            case FileHelper.FB2:
                return new Fb2BookDao(context);
        }

        return null;
    }
}
