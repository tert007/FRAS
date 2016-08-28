package com.example.alexander.fastreading.reader.bookparser;

import android.text.Spanned;

import com.example.alexander.fastreading.reader.FileHelper;

import java.util.List;

/**
 * Created by Alexander on 27.08.2016.
 */
public class BookParserFactory {
    public static Spanned getScrollSpannedText(String filePath) throws BookParserException{
        MyBookParser bookParser = getBookParser(filePath);

        return bookParser.getScrollSpannedText(filePath);
    }

    public static List<HtmlTag> getHtmlTagsText(String filePath) throws BookParserException{
        MyBookParser bookParser = getBookParser(filePath);

        return bookParser.getHtmlTagsText(filePath);
    }
    private static MyBookParser getBookParser(String filePath) {
        String fileExtension = FileHelper.getFileExtension(filePath);

        switch (fileExtension){
            case FileHelper.TXT:
                return MyTxtParser.getInstance();
            case FileHelper.EPUB:
                return MyEpubParser.getInstance();
            default:
                return null;
        }
    }
}
