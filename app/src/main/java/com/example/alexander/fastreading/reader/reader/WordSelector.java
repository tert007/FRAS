package com.example.alexander.fastreading.reader.reader;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 28.08.2016.
 */
public class WordSelector {

    private final static String regExp = "\\b([\\w]+)\\b";
    private final static Pattern pattern = Pattern.compile(regExp);

    private CharSequence page;

    private Matcher matcher;

    public WordSelector(CharSequence page){
        this.page = page;
        this.matcher = pattern.matcher(page);
    }

    public WorldSelectorPage getNextSelectedWord(){
        if (matcher.find()){
            SpannableString spannableString;

            int startIndex = matcher.start();
            int endIndex = matcher.end();

            spannableString = new SpannableString(page);
            spannableString.setSpan(new BackgroundColorSpan(Color.CYAN), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return new WorldSelectorPage(spannableString, endIndex - startIndex);
        }

        return null;
    }
}
