package com.example.alexander.fastreading.reader.bookparser;

import android.text.Spanned;

import com.example.alexander.fastreading.reader.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 27.08.2016.
 */
public class MyTxtParser implements MyBookParser {
    private static MyTxtParser instance = new MyTxtParser();
    private MyTxtParser() {
    }

    public static MyTxtParser getInstance() {
        return instance;
    }

    @Override
    public CharSequence getScrollText(String filePath) throws BookParserException {
        try {
            return FileHelper.getTextFromFile(filePath);
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }

    @Override
    public List<CharSequence> getPagesText(String filePath) throws BookParserException {
        //Тесктовый файл - книга с одной главой
        try {
            List<CharSequence> result = new ArrayList<>();
            result.add(FileHelper.getTextFromFile(filePath));

            return result;
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }
}
