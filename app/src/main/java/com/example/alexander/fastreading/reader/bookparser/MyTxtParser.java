package com.example.alexander.fastreading.reader.bookparser;

import android.text.Spanned;

import com.example.alexander.fastreading.reader.FileHelper;

import java.io.IOException;
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
    public Spanned getScrollSpannedText(String filePath) throws BookParserException {
        return HtmlHelper.convertHtmlPageToSpanned(getHtmlTagsText(filePath));
    }

    @Override
    public List<HtmlTag> getHtmlTagsText(String filePath) throws BookParserException {
        try {
            String textFromFile = FileHelper.getTextFromFile(filePath);
            return HtmlHelper.convertTextToHtmlPage(textFromFile);
        } catch (IOException e){
            throw new BookParserException(e);
        }
    }
}
