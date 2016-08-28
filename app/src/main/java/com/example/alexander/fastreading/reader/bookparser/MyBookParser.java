package com.example.alexander.fastreading.reader.bookparser;

import android.text.Spanned;

import java.io.IOException;
import java.util.List;

/**
 * Created by Alexander on 27.08.2016.
 */
public interface MyBookParser {
    Spanned getScrollSpannedText(String filePath) throws BookParserException;
    List<HtmlTag> getHtmlTagsText(String filePath) throws BookParserException;
}
