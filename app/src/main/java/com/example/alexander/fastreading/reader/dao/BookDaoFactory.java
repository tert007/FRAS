package com.example.alexander.fastreading.reader.dao;

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

    public BookDao getBookDao(String bookFilePath) {
        switch (FileHelper.getFileExtension(bookFilePath)){
            case FileHelper.EPUB:
                return new EpubBookDao(context);
            case FileHelper.TXT:
                return new TxtBookDao(context);
        }

        return null;
    }
}
